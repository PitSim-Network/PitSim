package dev.kyro.pitsim.megastreaks;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.pitsim.battlepass.quests.daily.DailyMegastreakQuest;
import dev.kyro.pitsim.controllers.objects.Megastreak;
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

public class Rampage extends Megastreak {
	public static Rampage INSTANCE;

	public Rampage() {
		super("&9Rampage", "rampage", 50, 5, 0);
		INSTANCE = this;
	}

	@EventHandler
	public void onHit(AttackEvent.Apply attackEvent) {
		if(!hasMegastreak(attackEvent.getAttackerPlayer())) return;
		PitPlayer pitPlayer = attackEvent.getAttackerPitPlayer();
		if(!pitPlayer.isOnMega()) return;
		int increments = getKillIncrements(pitPlayer, 20, 200);
		attackEvent.selfVeryTrueDamage += getSelfVeryTrueDamage() * increments;
		attackEvent.increasePercent += getStreakDamageIncrease() * increments;
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

//		TODO: Increase damage vs bots for time
	}

	@Override
	public String getPrefix(Player player) {
		return "&9&lRMPGE";
	}

	@Override
	public ItemStack getBaseDisplayStack(Player player) {
		return new AItemStackBuilder(Material.WHEAT)
				.getItemStack();
	}

	@Override
	public void addBaseDescription(PitLoreBuilder loreBuilder, PitPlayer pitPlayer) {
		loreBuilder.addLore(
				"&7On Trigger:",
				"&a\u25a0 &7Earn &b+" + getXPIncrease() + "% XP &7from kills",
				"&a\u25a0 &7Gain &b+" + getMaxXPIncrease() + " max XP &7from kills",
				"&a\u25a0 &7Deal &c+" + getStreakDamageIncrease() + "% &7damage to bots per 10",
				"   &7kills past " + requiredKills,
				"",
				"&7BUT:",
				"&c\u25a0 &7Starting at 200 kills, take &c+" + Misc.getHearts(getSelfVeryTrueDamage()),
				"   &7very true damage when attacking",
				"   &7per 20 kills",
				"&c\u25a0 &7Earn &c-50% &7gold from kills",
				"",
				"&7On Death:",
				"&e\u25a0 &7Deal &c+" + getPostDamageIncrease() + "% &7damage to bots for",
				"   &7the next &f" + Formatter.formatDurationMostSignificant(getPostDamageTicks() / 20) + " &7(non-stacking)"
		);
	}

	@Override
	public String getSummary() {
		return getCapsDisplayName() + "&7 is a Megastreak that";
	}

	public static int getXPIncrease() {
		return 75;
	}

	public static int getMaxXPIncrease() {
		return 50;
	}

	public static int getStreakDamageIncrease() {
		return 10;
	}

	public static double getSelfVeryTrueDamage() {
		return 0.2;
	}

	public static long getPostDamageIncrease() {
		return 20;
	}

	public static long getPostDamageTicks() {
		return 20 * 60 * 5;
	}
}
