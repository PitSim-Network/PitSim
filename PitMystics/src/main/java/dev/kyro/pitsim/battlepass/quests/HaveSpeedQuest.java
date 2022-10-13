package dev.kyro.pitsim.battlepass.quests;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.battlepass.PassQuest;
import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.SpawnManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class HaveSpeedQuest extends PassQuest {
	public static HaveSpeedQuest INSTANCE;

	public HaveSpeedQuest() {
		super("&f&lZooooom", "havespeed", QuestType.WEEKLY);
		INSTANCE = this;
	}

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				if(!HaveSpeedQuest.INSTANCE.isQuestActive()) return;
				for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
					if(!onlinePlayer.hasPotionEffect(PotionEffectType.SPEED) ||
							SpawnManager.isInSpawn(onlinePlayer.getLocation()) || MapManager.inDarkzone(onlinePlayer)) continue;
					PitPlayer pitPlayer = PitPlayer.getPitPlayer(onlinePlayer);
					HaveSpeedQuest.INSTANCE.progressQuest(pitPlayer, 1);
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0, 20);
	}

	@Override
	public ItemStack getDisplayItem(PitPlayer pitPlayer, QuestLevel questLevel, double progress) {
		ItemStack itemStack = new AItemStackBuilder(Material.SUGAR)
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"&7Have speed for &f" + Misc.formatLarge(questLevel.getRequirement(pitPlayer) / 60) + " &7minutes",
						"&7(not counting in spawn or in",
						"&7the darkzone)",
						"",
						"&7Progress: &3" + Misc.formatLarge(progress / 60) + "&7/&3" + Misc.formatLarge(questLevel.getRequirement(pitPlayer) / 60) + " &8[" +
								AUtil.createProgressBar("|", ChatColor.AQUA, ChatColor.GRAY, 20,
								progress / questLevel.getRequirement(pitPlayer)) + "&8]",
						"&7Reward: &3" + questLevel.rewardPoints + " &7Quest Points"
				))
				.getItemStack();
		return itemStack;
	}

	@Override
	public QuestLevel getDailyState() {
		return null;
	}

	@Override
	public List<QuestLevel> getWeeklyPossibleStates() {
		List<QuestLevel> questLevels = new ArrayList<>();
		questLevels.add(new QuestLevel(60 * 45, 100));
		return questLevels;
	}

	@Override
	public double getMultiplier(PitPlayer pitPlayer) {
		return 1.0;
	}
}
