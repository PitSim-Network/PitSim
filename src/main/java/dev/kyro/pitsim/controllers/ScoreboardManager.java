package dev.kyro.pitsim.controllers;

import be.maximvdw.featherboard.api.FeatherBoardAPI;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.settings.scoreboard.ScoreboardOption;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class ScoreboardManager implements Listener {
	public static List<ScoreboardOption> scoreboardOptions = new ArrayList<>();
	public static List<String> goldEnchants = Arrays.asList("moctezuma", "goldboost", "goldbump");
	public static Map<Player, PitScoreboard> currentScoreboardMap = new HashMap<>();

	public static void init() {}

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				for(Player onlinePlayer : Bukkit.getOnlinePlayers()) updateScoreboard(onlinePlayer);
			}
		}.runTaskTimerAsynchronously(PitSim.INSTANCE, 0, 20);
	}

	public static void registerScoreboard(ScoreboardOption option) {
		scoreboardOptions.add(option);
	}

	public static ScoreboardOption getScoreboardOption(String refName) {
		for(ScoreboardOption scoreboardOption : scoreboardOptions) if(scoreboardOption.getRefName().equals(refName)) return scoreboardOption;
		return null;
	}

	public static void updateScoreboard(Player player) {
		currentScoreboardMap.putIfAbsent(player, null);
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

		ItemStack leggings = player.getInventory().getLeggings();
		int goldEnchantCount = 0;
		for(Map.Entry<PitEnchant, Integer> entry : EnchantManager.getEnchantsOnItem(leggings).entrySet()) {
			if(goldEnchants.contains(entry.getKey().refNames.get(0))) goldEnchantCount++;
		}

		PitScoreboard expectedScoreboard;
		if(PitSim.status.isDarkzone()) {
			if(pitPlayer.scoreboardData.hasCustomScoreboardEnabled()) {
				expectedScoreboard = PitScoreboard.DARKZONE_CUSTOM;
			} else {
				expectedScoreboard = PitScoreboard.DARKZONE;
			}
		} else if(goldEnchantCount >= 2) {
			if(pitPlayer.scoreboardData.hasCustomScoreboardEnabled()) {
				expectedScoreboard = PitScoreboard.GOLD_CUSTOM;
			} else {
				expectedScoreboard = PitScoreboard.GOLD;
			}
		} else {
			if(pitPlayer.scoreboardData.hasCustomScoreboardEnabled()) {
				expectedScoreboard = PitScoreboard.DEFAULT_CUSTOM;
			} else {
				expectedScoreboard = PitScoreboard.DEFAULT;
			}
		}

		if(currentScoreboardMap.get(player) != expectedScoreboard) {
			expectedScoreboard.display(player);
			currentScoreboardMap.put(player, expectedScoreboard);
		}
	}

	public enum PitScoreboard {
		DEFAULT("default"),
		GOLD("gold"),
		DARKZONE("darkzone"),
		DEFAULT_CUSTOM("default-custom"),
		GOLD_CUSTOM("gold-custom"),
		DARKZONE_CUSTOM("darkzone-custom");

		public final String scoreboardName;

		PitScoreboard(String scoreboardName) {
			this.scoreboardName = scoreboardName;
		}

		public void display(Player player) {
			FeatherBoardAPI.resetDefaultScoreboard(player);
			FeatherBoardAPI.showScoreboard(player, scoreboardName);
		}
	}
}
