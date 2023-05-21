package dev.kyro.pitsim.megastreaks;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.battlepass.quests.daily.DailyMegastreakQuest;
import dev.kyro.pitsim.controllers.NonManager;
import dev.kyro.pitsim.controllers.objects.Megastreak;
import dev.kyro.pitsim.controllers.objects.Non;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.misc.Formatter;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class Rampage extends Megastreak {
	public static Rampage INSTANCE;
	private static final Map<Player, Integer> damageIncreaseMap = new HashMap<>();

	public Rampage() {
		super("&9Rampage", "rampage", 30, 4, 0);
		INSTANCE = this;
	}

	public static void addDamageStack(Player player) {
		damageIncreaseMap.put(player, damageIncreaseMap.getOrDefault(player, 0) + 1);
		new BukkitRunnable() {
			@Override
			public void run() {
				damageIncreaseMap.put(player, damageIncreaseMap.get(player) - 1);
			}
		}.runTaskLater(PitSim.INSTANCE, getPostDamageTicks());
	}

	public static int getDamageStacks(PitPlayer pitPlayer) {
		if(pitPlayer == null) return 0;
		return damageIncreaseMap.getOrDefault(pitPlayer.player, 0);
	}

	@EventHandler
	public void onHit(AttackEvent.Apply attackEvent) {
		if(!hasMegastreak(attackEvent.getAttackerPlayer())) return;

		Non defendingNon = NonManager.getNon(attackEvent.getDefender());
		if(defendingNon != null) attackEvent.increasePercent += getPostDamageIncrease() *
				getDamageStacks(attackEvent.getAttackerPitPlayer());

		PitPlayer pitPlayer = attackEvent.getAttackerPitPlayer();
		if(!pitPlayer.isOnMega()) return;

		int selfDamageIncrements = getKillIncrements(pitPlayer, getSelfDamageInterval(), requiredKills);
		if(!attackEvent.isFakeHit()) attackEvent.selfVeryTrueDamage = getSelfDamage() * selfDamageIncrements;

		int streakDamageIncrements = getKillIncrements(pitPlayer, getStreakDamageInterval(), requiredKills);
		attackEvent.increasePercent += getStreakDamageIncrease() * streakDamageIncrements;
	}

	@EventHandler
	public void kill(KillEvent killEvent) {
		if(!hasMegastreak(killEvent.getKillerPlayer())) return;
		PitPlayer pitPlayer = killEvent.getKillerPitPlayer();
		if(!pitPlayer.isOnMega()) return;
		killEvent.xpMultipliers.add(1 + (getXPIncrease() / 100.0));
		killEvent.xpCap += getMaxXPIncrease();
		killEvent.goldMultipliers.add(0.5);
	}

	@Override
	public void proc(Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

		Sounds.MEGA_GENERAL.play(player.getLocation());
		pitPlayer.stats.timesOnRampage++;
		DailyMegastreakQuest.INSTANCE.onMegastreakComplete(pitPlayer);
	}

	@Override
	public void reset(Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		if(!pitPlayer.isOnMega()) return;

		addDamageStack(player);
		AOutput.send(pitPlayer.player, getCapsDisplayName() + "!&7 Damage vs bots increased by &c+" +
						getPostDamageIncrease() + "% &7for &f" + Formatter.formatDurationMostSignificant(getPostDamageTicks() / 20) + "&7!");
	}

	@Override
	public String getPrefix(Player player) {
		return "&9&lRMPGE";
	}

	@Override
	public ItemStack getBaseDisplayStack(Player player) {
		return new AItemStackBuilder(Material.IRON_AXE)
				.getItemStack();
	}

	@Override
	public void addBaseDescription(PitLoreBuilder loreBuilder, PitPlayer pitPlayer) {
		loreBuilder.addLore(
				"&7On Trigger:",
				"&a\u25a0 &7Earn &b+" + getXPIncrease() + "% XP &7from kills",
				"&a\u25a0 &7Gain &b+" + getMaxXPIncrease() + " max XP &7from kills",
				"&a\u25a0 &7Deal &c+" + getStreakDamageIncrease() + "% &7damage to bots per " + getStreakDamageInterval(),
				"   &7kills past " + requiredKills,
				"",
				"&7BUT:",
				"&c\u25a0 &7Take &c+" + Misc.getHearts(getSelfDamage()) + " &7very true damage",
				"   &7when attacking per " + getSelfDamageInterval() + " kills past " + requiredKills,
				"&c\u25a0 &7Earn &c-50% &7gold from kills",
				"",
				"&7On Death:",
				"&e\u25a0 &7Deal &c+" + getPostDamageIncrease() + "% &7damage (stacking) to bots",
				"   &7for the next &f" + Formatter.formatDurationMostSignificant(getPostDamageTicks() / 20) + " &7while",
				"   &7this streak is equipped"
		);
	}

	@Override
	public String getSummary() {
		return getCapsDisplayName() + "&7 is a Megastreak that";
	}

	public static int getXPIncrease() {
		return 120;
	}

	public static int getMaxXPIncrease() {
		return 50;
	}

	public static int getStreakDamageIncrease() {
		return 15;
	}

	public static int getStreakDamageInterval() {
		return 10;
	}

	public static double getSelfDamage() {
		return 0.6;
	}

	public static int getSelfDamageInterval() {
		return 20;
	}

	public static long getPostDamageIncrease() {
		return 20;
	}

	public static long getPostDamageTicks() {
		return 20 * 60 * 5;
	}
}
