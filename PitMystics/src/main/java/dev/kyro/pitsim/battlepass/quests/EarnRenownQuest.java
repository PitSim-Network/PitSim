package dev.kyro.pitsim.battlepass.quests;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.battlepass.PassQuest;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class EarnRenownQuest extends PassQuest {
	public static EarnRenownQuest INSTANCE;

	public EarnRenownQuest() {
		super("&e&lPrestigious", "earnrenown", QuestType.WEEKLY);
		INSTANCE = this;
	}

	public void gainRenown(PitPlayer pitPlayer, int amount) {
		progressQuest(pitPlayer, amount);
	}

	@Override
	public ItemStack getDisplayItem(PitPlayer pitPlayer, QuestLevel questLevel, double progress) {
		DecimalFormat decimalFormat = new DecimalFormat("#,000");
		ItemStack itemStack = new AItemStackBuilder(Material.BEACON)
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"&7Earn &f" + decimalFormat.format(questLevel.getRequirement(pitPlayer)) + " renown",
						"",
						"&7Progress: &3" + decimalFormat.format(progress) + "&7/&3" + decimalFormat.format(questLevel.getRequirement(pitPlayer)) + " &8[" +
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
	public void createWeeklyPossibleStates() {
		questLevels.add(new QuestLevel(5, 100));
		questLevels.add(new QuestLevel(7, 150));
		questLevels.add(new QuestLevel(10, 200));
	}

	@Override
	public double getMultiplier(PitPlayer pitPlayer) {
		if(pitPlayer.prestige == 0) return 1;
		return Math.pow(pitPlayer.prestige, 2.0/3.0);
	}
}
