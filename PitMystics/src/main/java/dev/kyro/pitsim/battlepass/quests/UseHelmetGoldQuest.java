package dev.kyro.pitsim.battlepass.quests;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.battlepass.PassQuest;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class UseHelmetGoldQuest extends PassQuest {
	public static UseHelmetGoldQuest INSTANCE;

	public UseHelmetGoldQuest() {
		super("&6&lSuperfluous Spending", "usehelmetgold", QuestType.WEEKLY);
		INSTANCE = this;
	}

	public void progressTime(PitPlayer pitPlayer) {
		progressQuest(pitPlayer, 1);
	}

	@Override
	public ItemStack getDisplayItem(PitPlayer pitPlayer, QuestLevel questLevel, double progress) {
		ItemStack itemStack = new AItemStackBuilder(Material.GOLD_NUGGET)
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"&7Spend &e" + Misc.formatLarge(questLevel.getRequirement(pitPlayer) / 60) + " &7hours on the server",
						"",
						"&7Progress: &3" + Misc.formatLarge(progress / 60) + "&7/&3" + Misc.formatLarge(questLevel.getRequirement(pitPlayer) / 20) + " &8[" +
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
		questLevels.add(new QuestLevel(10 * 60, 100));
		return questLevels;
	}

	@Override
	public double getMultiplier(PitPlayer pitPlayer) {
		return 1.0;
	}
}
