package dev.kyro.pitsim.controllers;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticguilds.BuffManager;
import dev.kyro.arcticguilds.GuildBuff;
import dev.kyro.arcticguilds.GuildData;
import dev.kyro.arcticguilds.events.GuildWithdrawalEvent;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

public class GuildIntegrationManager implements Listener {

	//	Reputation amounts
	public static int getIdleReputation() {
		return (int) (Math.random() * 40);
	}

	public static int getKillReputation() {
		return (int) (Math.random() * 300);
	}

	public static int getFeatherLossReputation() {
		return (int) (Math.random() * 2_000);
	}

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
					if(AFKManager.AFKPlayers.contains(onlinePlayer)) continue;
					GuildData guild = GuildData.getGuildData(onlinePlayer);
					if(guild == null) continue;
					guild.addReputation(getIdleReputation());
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, Misc.getRunnableOffset(1), 20 * 60);
	}

	public static void handleFeather(LivingEntity checkKiller, Player dead) {
		if(checkKiller == null) return;
		if(!(checkKiller instanceof Player)) return;
		Player killer = (Player) checkKiller;

		GuildData killerGuild = GuildData.getGuildData(killer);
		GuildData deadGuild = GuildData.getGuildData(dead);

		if(killerGuild != null && deadGuild != null) {
			killerGuild.addReputation(getFeatherLossReputation());
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!attackEvent.attackerIsPlayer || !attackEvent.defenderIsPlayer) return;

		GuildData attackerGuild = GuildData.getGuildData(attackEvent.attackerPlayer);
		GuildData defenderGuild = GuildData.getGuildData(attackEvent.defenderPlayer);
		if(attackerGuild == null || defenderGuild == null || attackerGuild == defenderGuild) return;

		GuildBuff damageBuff = BuffManager.getBuff("damage");
		int damageBuffLevel = attackerGuild.getBuffLevel(damageBuff);
		if(damageBuffLevel != 0) {
			Map<GuildBuff.SubBuff, Double> buffs = damageBuff.getBuffs(damageBuffLevel);
			for(Map.Entry<GuildBuff.SubBuff, Double> entry : buffs.entrySet()) {
				if(entry.getKey().refName.equals("damage")) attackEvent.increasePercent += entry.getValue() / 100.0;
				else if(entry.getKey().refName.equals("truedamage")) {
					if(attackEvent.trueDamage != 0) attackEvent.trueDamage += entry.getValue();
				}
			}
		}

		GuildBuff defenceBuff = BuffManager.getBuff("defence");
		int defenceBuffLevel = defenderGuild.getBuffLevel(defenceBuff);
		if(defenceBuffLevel != 0) {
			Map<GuildBuff.SubBuff, Double> buffs = defenceBuff.getBuffs(defenceBuffLevel);
			for(Map.Entry<GuildBuff.SubBuff, Double> entry : buffs.entrySet()) {
				if(entry.getKey().refName.equals("defence"))
					attackEvent.multipliers.add(Misc.getReductionMultiplier(entry.getValue()));
				else if(entry.getKey().refName.equals("truedefence"))
					attackEvent.trueDamage = Math.max(attackEvent.trueDamage - entry.getValue(), 0);
			}
		}
	}

	//	@EventHandler(priority = EventPriority.HIGH)
	public void onAttack(AttackEvent.Pre attackEvent) {
		if(!attackEvent.attackerIsPlayer || !attackEvent.defenderIsPlayer) return;

		GuildData attackerGuild = GuildData.getGuildData(attackEvent.attackerPlayer);
		GuildData defenderGuild = GuildData.getGuildData(attackEvent.defenderPlayer);
		if(attackerGuild == null || defenderGuild == null || attackerGuild == defenderGuild) return;

		GuildBuff dispersionBuff = BuffManager.getBuff("dispersion");
		int dispersionBuffLevel = attackerGuild.getBuffLevel(dispersionBuff);
		if(dispersionBuffLevel != 0) {
			Map<GuildBuff.SubBuff, Double> buffs = dispersionBuff.getBuffs(dispersionBuffLevel);
			for(Map.Entry<GuildBuff.SubBuff, Double> entry : buffs.entrySet()) {
				double chance = entry.getValue() / 100.0;

				break;
			}
		}
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(!killEvent.killerIsPlayer || !killEvent.deadIsPlayer) return;

		GuildData killerGuild = GuildData.getGuildData(killEvent.killerPlayer);
		GuildData deadGuild = GuildData.getGuildData(killEvent.deadPlayer);
		if(killerGuild == null || killerGuild == deadGuild) return;

		GuildBuff xpBuff = BuffManager.getBuff("xp");
		int xpBuffLevel = killerGuild.getBuffLevel(xpBuff);
		if(xpBuffLevel != 0) {
			Map<GuildBuff.SubBuff, Double> buffs = xpBuff.getBuffs(xpBuffLevel);
			for(Map.Entry<GuildBuff.SubBuff, Double> entry : buffs.entrySet()) {
				if(entry.getKey().refName.equals("xp")) killEvent.xpMultipliers.add(1 + entry.getValue() / 100.0);
				if(entry.getKey().refName.equals("maxxp")) killEvent.maxXPMultipliers.add(1 + entry.getValue() / 100.0);
			}
		}

		GuildBuff goldBuff = BuffManager.getBuff("gold");
		int goldBuffLevel = killerGuild.getBuffLevel(goldBuff);
		if(goldBuffLevel != 0) {
			Map<GuildBuff.SubBuff, Double> buffs = goldBuff.getBuffs(goldBuffLevel);
			for(Map.Entry<GuildBuff.SubBuff, Double> entry : buffs.entrySet()) {
				if(entry.getKey().refName.equals("gold")) killEvent.goldMultipliers.add(1 + entry.getValue() / 100.0);
			}
		}

		if(deadGuild != null) {
			killerGuild.addReputation(getKillReputation());
		}
	}

	@EventHandler
	public void onWithdrawal(GuildWithdrawalEvent event) {
		System.out.println("Withdrawal event");

		Player player = event.getPlayer();
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		if(pitPlayer.level < 100) {
			event.setCancelled(true);
			AOutput.error(event.getPlayer(), "&c&lNOPE! &7You cannot withdraw until you are level 100");
		}
	}
}
