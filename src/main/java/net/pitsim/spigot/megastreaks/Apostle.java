package net.pitsim.spigot.megastreaks;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import net.pitsim.spigot.PitSim;
import net.pitsim.spigot.battlepass.quests.daily.DailyMegastreakQuest;
import net.pitsim.spigot.controllers.LevelManager;
import net.pitsim.spigot.controllers.objects.Megastreak;
import net.pitsim.spigot.controllers.objects.PitPlayer;
import net.pitsim.spigot.events.KillEvent;
import net.pitsim.spigot.misc.Formatter;
import net.pitsim.spigot.misc.Misc;
import net.pitsim.spigot.misc.PitLoreBuilder;
import net.pitsim.spigot.misc.Sounds;
import net.pitsim.spigot.upgrades.DoubleDeath;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class Apostle extends Megastreak {
	public static Apostle INSTANCE;
	public static Map<Player, Long> storedXPMap = new HashMap<>();

	public static final int DEATH_BONUS = 3;

	public Apostle() {
		super("&3Apostle", "apostle", 100, 42, 110);
		INSTANCE = this;

		new BukkitRunnable() {
			@Override
			public void run() {
				for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
					PitPlayer pitPlayer = PitPlayer.getPitPlayer(onlinePlayer);
					if(!(pitPlayer.getMegastreak() instanceof Apostle) || !pitPlayer.isOnMega()) continue;
					Misc.sendActionBar(onlinePlayer, "&7Stored XP: &b" +
							Formatter.commaFormat.format(storedXPMap.getOrDefault(onlinePlayer, 0L)) +
							" &7(&b" + Formatter.decimalCommaFormat.format(getMultiplier(pitPlayer)) + "x&7)");
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0, 1L);
	}

	public static int getRemovedHealth(PitPlayer pitPlayer) {
		if(!(pitPlayer.getMegastreak() instanceof Apostle) || !pitPlayer.isOnMega()) return 0;
		return ((pitPlayer.getKills() - getStartingHeartsReduction()) / getFrequencyHeartsReduction() + 1) * 2;
	}

	public static double getMultiplier(PitPlayer pitPlayer) {
		if(!pitPlayer.isOnMega()) return 0;
		return Math.min(getPerKillMultiplier() * (pitPlayer.getKills() - INSTANCE.requiredKills), getMaxMultiplier());
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(!hasMegastreak(killEvent.getKillerPlayer())) return;
		PitPlayer pitPlayer = killEvent.getKillerPitPlayer();
		if(!pitPlayer.isOnMega()) return;
		killEvent.xpMultipliers.add(1 + (getXPIncrease() / 100.0));
		killEvent.xpCap += getInitialMaxXP() + pitPlayer.apostleBonus;
		killEvent.goldMultipliers.add(0.5);
		pitPlayer.updateMaxHealth();
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onKillMonitor(KillEvent killEvent) {
		if(!hasMegastreak(killEvent.getKillerPlayer())) return;
		PitPlayer pitPlayer = killEvent.getKillerPitPlayer();
		if(!pitPlayer.isOnMega()) return;
		storedXPMap.put(killEvent.getKillerPlayer(), storedXPMap.getOrDefault(killEvent.getKillerPlayer(), 0L) + killEvent.getFinalXp());
	}

	@Override
	public void proc(Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

		Sounds.MEGA_GENERAL.play(player.getLocation());
		pitPlayer.stats.timesOnApostle++;
		DailyMegastreakQuest.INSTANCE.onMegastreakComplete(pitPlayer);
	}

	@Override
	public void reset(Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		if(!pitPlayer.isOnMega()) return;

		int doubleDeathMultiplier = DoubleDeath.INSTANCE.isDoubleDeath(pitPlayer.player) ? 2 : 1;

		if(storedXPMap.containsKey(player)) {
			long finalXP = (long) (storedXPMap.remove(player) * getMultiplier(pitPlayer) * doubleDeathMultiplier);
			AOutput.send(pitPlayer.player, getCapsDisplayName() + "!&7 Earned &b" + Formatter.commaFormat.format(finalXP) +
					"&b XP &7from megastreak!");
			LevelManager.addXP(pitPlayer.player, finalXP);
		}

		if(pitPlayer.getKills() >= 1000) {
			int apostleIncrease = Math.min(DEATH_BONUS * doubleDeathMultiplier, getMaxMaxXPIncrease() - pitPlayer.apostleBonus);
			if(apostleIncrease != 0) {
				pitPlayer.apostleBonus += apostleIncrease;
				AOutput.send(pitPlayer.player, getCapsDisplayName() + "!&7 Permanent &b+" + apostleIncrease +
						" max XP &7while using this megastreak! (&b" + pitPlayer.apostleBonus + "&7/&b" + getMaxMaxXPIncrease() + "&7)");
			}
		}
	}

	@Override
	public String getPrefix(Player player) {
		return "&3&lAPSTL";
	}

	@Override
	public ItemStack getBaseDisplayStack(Player player) {
		return new AItemStackBuilder(Material.STEP, 1, 7)
				.getItemStack();
	}

	@Override
	public void addBaseDescription(PitLoreBuilder loreBuilder, PitPlayer pitPlayer) {
		loreBuilder.addLore(
				"&7On Trigger:",
				"&a\u25a0 &7Earn &b+" + getXPIncrease() + "% XP &7from kills",
				"&a\u25a0 &7Gain &b+" + (getInitialMaxXP() + pitPlayer.apostleBonus) + " max XP &7from kills",
				"",
				"&7BUT:",
				"&c\u25a0 &7Starting at " + getStartingHeartsReduction() + " kills, lose",
				"   &c1 max \u2764 &7every " + getFrequencyHeartsReduction() + " kills",
				"&c\u25a0 &7Earn &c-50% &7gold from kills",
				"",
				"&7During the Streak:",
				"&e\u25a0 &7Store the &bXP &7you earn",
				"",
				"&7On Death:",
				"&e\u25a0 &7Earn the stored &bXP &7multiplied",
				"   &7by &b" + getPerKillMultiplier() + "x &7per kill above 100,",
				"   &7up to &b" + Formatter.decimalCommaFormat.format(getMaxMultiplier()) + "x",
				"&e\u25a0 &7If your streak is at least 1,000,",
				"   &7permanently alter this megastreak's",
				"   &bmax XP &7per kill by &b+2 &7(&b" + pitPlayer.apostleBonus + "&7/&b" + getMaxMaxXPIncrease() + "&7)"
		);
	}

	@Override
	public String getSummary() {
		return getCapsDisplayName() + "&7 is a Megastreak that";
	}

	public static int getXPIncrease() {
		return 140;
	}

	public static int getInitialMaxXP() {
		return 100;
	}

	public static int getMaxMaxXPIncrease() {
		return 500;
	}

	public static double getPerKillMultiplier() {
		return 0.002;
	}

	public static double getMaxMultiplier() {
		return 1;
	}

	public static int getStartingHeartsReduction() {
		return 200;
	}

	public static int getFrequencyHeartsReduction() {
		return 100;
	}
}
