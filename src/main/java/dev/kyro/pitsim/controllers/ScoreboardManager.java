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
	public static List<String> goldEnchants = Arrays.asList("moct", "gboost", "gbump");
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
		for(ScoreboardOption scoreboardOption : scoreboardOptions) if(scoreboardOption.refName.equals(refName)) return scoreboardOption;
		return null;
	}

	public static void updateScoreboard(Player player) {
		currentScoreboardMap.putIfAbsent(player, null);
		if(PitSim.status.isDarkzone()) {
			if(currentScoreboardMap.get(player) != PitScoreboard.DARKZONE) {
				PitScoreboard.DARKZONE.display(player);
				currentScoreboardMap.put(player, PitScoreboard.DARKZONE);
			}
			return;
		}

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

		ItemStack leggings = player.getInventory().getLeggings();
		int goldEnchantCount = 0;
		for(Map.Entry<PitEnchant, Integer> entry : EnchantManager.getEnchantsOnItem(leggings).entrySet()) {
			if(goldEnchants.contains(entry.getKey().refNames.get(0))) goldEnchantCount++;
		}

		PitScoreboard expectedScoreboard;
		if(goldEnchantCount >= 2) {
			if(pitPlayer.scoreboardData.hasCustomScoreboardEnabled()) {
				expectedScoreboard = PitScoreboard.GOLD_WITH_EXTRA;
			} else {
				expectedScoreboard = PitScoreboard.GOLD;
			}
		} else {
			if(pitPlayer.scoreboardData.hasCustomScoreboardEnabled()) {
				expectedScoreboard = PitScoreboard.DEFAULT_WITH_EXTRA;
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
		DEFAULT_WITH_EXTRA("default-custom"),
		GOLD_WITH_EXTRA("gold-custom"),
		DARKZONE("darkzone");

		public final String scoreboardName;

		PitScoreboard(String scoreboardName) {
			this.scoreboardName = scoreboardName;
		}

		public void display(Player player) {
			FeatherBoardAPI.showScoreboard(player, scoreboardName);
		}
	}
}
