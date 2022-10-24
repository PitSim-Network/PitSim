package dev.kyro.pitsim.controllers;

import de.myzelyam.api.vanish.VanishAPI;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticguilds.controllers.BuffManager;
import dev.kyro.arcticguilds.controllers.GuildManager;
import dev.kyro.arcticguilds.controllers.objects.Guild;
import dev.kyro.arcticguilds.controllers.objects.GuildBuff;
import dev.kyro.arcticguilds.events.GuildWithdrawalEvent;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.battlepass.quests.EarnGuildReputationQuest;
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
		return (int) (Math.random() * 1_000);
	}

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
					if(AFKManager.AFKPlayers.contains(onlinePlayer)) continue;
					if(VanishAPI.isInvisible(onlinePlayer)) continue;
					Guild guild = GuildManager.getGuild(onlinePlayer);
					if(guild == null) continue;
					guild.addReputation(getIdleReputation());
					PitPlayer pitPlayer = PitPlayer.getPitPlayer(onlinePlayer);
					EarnGuildReputationQuest.INSTANCE.gainReputation(pitPlayer, getIdleReputation());
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, Misc.getRunnableOffset(1), 20 * 60);
	}

	public static void handleFeather(LivingEntity checkKiller, Player dead) {
		if(checkKiller == null) return;
		if(!(checkKiller instanceof Player)) return;
		Player killer = (Player) checkKiller;

		Guild killerGuild = GuildManager.getGuild(killer);
		Guild deadGuild = GuildManager.getGuild(dead);

		if(killerGuild != null && deadGuild != null) {
			killerGuild.addReputation(getFeatherLossReputation());
			PitPlayer pitPlayer = PitPlayer.getPitPlayer(killer);
			EarnGuildReputationQuest.INSTANCE.gainReputation(pitPlayer, getFeatherLossReputation());
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!attackEvent.isAttackerPlayer() || !attackEvent.isDefenderPlayer()) return;

		Guild attackerGuild = GuildManager.getGuild(attackEvent.getAttackerPlayer());
		Guild defenderGuild = GuildManager.getGuild(attackEvent.getDefenderPlayer());
		if(attackerGuild == null || defenderGuild == null || attackerGuild == defenderGuild) return;

		GuildBuff damageBuff = BuffManager.getBuff("damage");
		int damageBuffLevel = attackerGuild.getLevel(damageBuff);
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
		int defenceBuffLevel = defenderGuild.getLevel(defenceBuff);
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
		if(!attackEvent.isAttackerPlayer() || !attackEvent.isDefenderPlayer()) return;

		Guild attackerGuild = GuildManager.getGuild(attackEvent.getAttackerPlayer());
		Guild defenderGuild = GuildManager.getGuild(attackEvent.getDefenderPlayer());
		if(attackerGuild == null || defenderGuild == null || attackerGuild == defenderGuild) return;

		GuildBuff dispersionBuff = BuffManager.getBuff("dispersion");
		int dispersionBuffLevel = attackerGuild.getLevel(dispersionBuff);
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
		if(!killEvent.isKillerPlayer() || !killEvent.isDeadPlayer()) return;

		Guild killerGuild = GuildManager.getGuild(killEvent.getKillerPlayer());
		Guild deadGuild = GuildManager.getGuild(killEvent.getDeadPlayer());
		if(killerGuild == null || killerGuild == deadGuild) return;

		GuildBuff xpBuff = BuffManager.getBuff("xp");
		int xpBuffLevel = killerGuild.getLevel(xpBuff);
		if(xpBuffLevel != 0) {
			Map<GuildBuff.SubBuff, Double> buffs = xpBuff.getBuffs(xpBuffLevel);
			for(Map.Entry<GuildBuff.SubBuff, Double> entry : buffs.entrySet()) {
				if(entry.getKey().refName.equals("xp")) killEvent.xpMultipliers.add(1 + entry.getValue() / 100.0);
				if(entry.getKey().refName.equals("maxxp")) killEvent.maxXPMultipliers.add(1 + entry.getValue() / 100.0);
			}
		}

		GuildBuff goldBuff = BuffManager.getBuff("gold");
		int goldBuffLevel = killerGuild.getLevel(goldBuff);
		if(goldBuffLevel != 0) {
			Map<GuildBuff.SubBuff, Double> buffs = goldBuff.getBuffs(goldBuffLevel);
			for(Map.Entry<GuildBuff.SubBuff, Double> entry : buffs.entrySet()) {
				if(entry.getKey().refName.equals("gold")) killEvent.goldMultipliers.add(1 + entry.getValue() / 100.0);
			}
		}

		if(deadGuild != null) {
			killerGuild.addReputation(getKillReputation());
			EarnGuildReputationQuest.INSTANCE.gainReputation(killEvent.getKillerPitPlayer(), getKillReputation());
		}
	}

//	@EventHandler
//	public void onReputation(GuildReputationEvent event) {
//		Guild guild = event.getGuild();
//
//		GuildBuff renownBuff = BuffManager.getBuff("renown");
//		int renownBuffLevel = guild.getLevel(renownBuff);
//		if(renownBuffLevel != 0) {
//			Map<GuildBuff.SubBuff, Double> buffs = renownBuff.getBuffs(renownBuffLevel);
//			for(Map.Entry<GuildBuff.SubBuff, Double> entry : buffs.entrySet()) {
//				event.addMultiplier(1 + entry.getValue() / 100.0);
//				break;
//			}
//		}
//	}

	@EventHandler
	public void onWithdrawal(GuildWithdrawalEvent event) {
		Player player = event.getPlayer();
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		if(pitPlayer.level < 100) {
			event.setCancelled(true);
			AOutput.error(event.getPlayer(), "&c&lNOPE! &7You cannot withdraw until you are level 100");
		}
	}
}
