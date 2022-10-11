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

public class CompleteUbersQuest extends PassQuest {
	public static CompleteUbersQuest INSTANCE;

	public CompleteUbersQuest() {
		super("&d&lProfit!", "uberscompleted", QuestType.WEEKLY);
		INSTANCE = this;
	}

	public void completeUber(PitPlayer pitPlayer) {
		progressQuest(pitPlayer, 1);
	}

	@Override
	public ItemStack getDisplayItem(PitPlayer pitPlayer, QuestLevel questLevel, double progress) {
		ItemStack itemStack = new AItemStackBuilder(Material.EMERALD)
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"&7Complete &d" + Misc.formatLarge(questLevel.getRequirement(pitPlayer)) + " Uberstreaks",
						"",
						"&7Progress: &3" + Misc.formatLarge(progress) + "&7/&3" + Misc.formatLarge(questLevel.getRequirement(pitPlayer)) + " &8[" +
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
	public List<QuestLevel> getWeeklyPossibleStates() {
		List<QuestLevel> questLevels = new ArrayList<>();
		questLevels.add(new QuestLevel(10.0, 100));
		return questLevels;
	}

	@Override
	public double getMultiplier(PitPlayer pitPlayer) {
		return 1.0;
	}
}
