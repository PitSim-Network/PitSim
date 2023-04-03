package dev.kyro.pitsim.battlepass.quests;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.battlepass.PassQuest;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.misc.Formatter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ReachKillstreakQuest extends PassQuest {
	public static ReachKillstreakQuest INSTANCE;
	public static final int KILLSTREAK_REQUIREMENT = 100;

	public ReachKillstreakQuest() {
		super("&e&lGod Tier Streaker", "reachkillstreak", QuestType.WEEKLY);
		INSTANCE = this;
	}

	public void endStreak(PitPlayer pitPlayer, int streak) {
		if(streak >= KILLSTREAK_REQUIREMENT) progressQuest(pitPlayer, 1);
	}

	@Override
	public ItemStack getDisplayStack(PitPlayer pitPlayer, QuestLevel questLevel, double progress) {
		ItemStack itemStack = new AItemStackBuilder(Material.BLAZE_POWDER)
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"&7Reach a killstreak of at",
						"&7least " + KILLSTREAK_REQUIREMENT + " &e" + Formatter.formatLarge(questLevel.getRequirement(pitPlayer)) + " &7times",
						"",
						"&7Progress: &3" + Formatter.formatLarge(progress) + "&7/&3" + Formatter.formatLarge(questLevel.getRequirement(pitPlayer)) + " &8[" +
								AUtil.createProgressBar("|", ChatColor.AQUA, ChatColor.GRAY, 20, progress / questLevel.getRequirement(pitPlayer)) + "&8]",
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
	public void createPossibleStates() {
		questLevels.add(new QuestLevel(100, 100));
		questLevels.add(new QuestLevel(150, 150));
		questLevels.add(new QuestLevel(200, 200));
	}

	@Override
	public double getMultiplier(PitPlayer pitPlayer) {
		return 1.0;
	}
}
