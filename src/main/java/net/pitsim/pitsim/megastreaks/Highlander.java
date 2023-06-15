package net.pitsim.pitsim.megastreaks;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import net.pitsim.pitsim.PitSim;
import net.pitsim.pitsim.battlepass.quests.daily.DailyMegastreakQuest;
import net.pitsim.pitsim.controllers.ChatTriggerManager;
import net.pitsim.pitsim.controllers.LevelManager;
import net.pitsim.pitsim.controllers.NonManager;
import net.pitsim.pitsim.controllers.objects.Megastreak;
import net.pitsim.pitsim.controllers.objects.PitPlayer;
import net.pitsim.pitsim.events.AttackEvent;
import net.pitsim.pitsim.events.HealEvent;
import net.pitsim.pitsim.events.KillEvent;
import net.pitsim.pitsim.misc.Misc;
import net.pitsim.pitsim.misc.PitLoreBuilder;
import net.pitsim.pitsim.misc.Sounds;
import net.pitsim.pitsim.upgrades.DoubleDeath;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;

public class Highlander extends Megastreak {
	public static Highlander INSTANCE;

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
					PitPlayer pitPlayer = PitPlayer.getPitPlayer(onlinePlayer);
					if(pitPlayer.getMegastreak() != Overdrive.INSTANCE || !pitPlayer.isOnMega()) continue;
					Misc.applyPotionEffect(onlinePlayer, PotionEffectType.SPEED, 200, 0, true, false);
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 60L);
	}

	public Highlander() {
		super("&6Highlander", "highlander", 50, 17, 90);
		INSTANCE = this;
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(!hasMegastreak(killEvent.getKillerPlayer())) return;
		PitPlayer pitPlayer = killEvent.getKillerPitPlayer();
		if(!pitPlayer.isOnMega()) return;
		killEvent.goldMultipliers.add(1 + (getGoldIncrease() / 100.0));
		killEvent.xpMultipliers.add(0.5);
	}

	@EventHandler
	public void ohHeal(HealEvent healEvent) {
		if(!hasMegastreak(healEvent.getPlayer())) return;
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(healEvent.getPlayer());
		if(!pitPlayer.isOnMega()) return;
		if(pitPlayer.getKills() > 200) healEvent.multipliers.add(1 / ((pitPlayer.getKills() - 200) / 50.0 + 1));
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!hasMegastreak(attackEvent.getAttackerPlayer())) return;
		PitPlayer pitPlayer = attackEvent.getAttackerPitPlayer();
		if(!pitPlayer.isOnMega() || NonManager.getNon(attackEvent.getDefender()) == null) return;
		attackEvent.increasePercent += getDamageIncrease();
	}

	@Override
	public void proc(Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

		Sounds.MEGA_GENERAL.play(pitPlayer.player.getLocation());
		pitPlayer.stats.timesOnHighlander++;
		DailyMegastreakQuest.INSTANCE.onMegastreakComplete(pitPlayer);
	}

	@Override
	public void reset(Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		if(!pitPlayer.isOnMega()) return;

		if(DoubleDeath.INSTANCE.isDoubleDeath(pitPlayer.player)) pitPlayer.bounty = pitPlayer.bounty * 2;
		LevelManager.addGold(pitPlayer.player, pitPlayer.bounty);
		if(pitPlayer.bounty != 0 && pitPlayer.isOnMega()) {
			DecimalFormat formatter = new DecimalFormat("#,###.#");
			AOutput.send(pitPlayer.player, getCapsDisplayName() + "!&7 Earned &6+" + formatter.format(pitPlayer.bounty) + "&6g &7from megastreak!");
			pitPlayer.bounty = 0;
			ChatTriggerManager.sendBountyInfo(pitPlayer);
		}
	}

	@Override
	public String getPrefix(Player player) {
		return "&6&lHIGH";
	}


	@Override
	public ItemStack getBaseDisplayStack(Player player) {
		return new AItemStackBuilder(Material.GOLD_BOOTS)
				.getItemStack();
	}

	@Override
	public void addBaseDescription(PitLoreBuilder loreBuilder, PitPlayer pitPlayer) {
		loreBuilder.addLore(
				"&7On Trigger:",
				"&a\u25a0 &7Perma &eSpeed I&7",
				"&a\u25a0 &7Earn &6+" + getGoldIncrease() + "% gold &7from kills",
				"&a\u25a0 &7Deal &c+" + getDamageIncrease() + "% &7damage vs bots",
				"",
				"&7BUT:",
				"&c\u25a0 &7Heal &cless &7per kill over 200",
				"&c\u25a0 &7Earn &c-50% &7XP from kills",
				"",
				"&7On Death:",
				"&e\u25a0 &7Earn your own bounty as well"
		);
	}

	@Override
	public String getSummary() {
		return getCapsDisplayName() + "&7 is a Megastreak grants you increased &6Gold, permanent &espeed I&7, more damage " +
				"to bots, and gain your bounty on death, but heal less per kill over 200, and earn less &bXP";
	}

	public static int getGoldIncrease() {
		return 110;
	}

	public static int getDamageIncrease() {
		return 25;
	}
}
