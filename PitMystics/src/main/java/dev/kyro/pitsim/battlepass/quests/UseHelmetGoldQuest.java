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

	public void spendGold(PitPlayer pitPlayer, int gold) {
		progressQuest(pitPlayer, gold);
	}

	@Override
	public ItemStack getDisplayItem(PitPlayer pitPlayer, QuestLevel questLevel, double progress) {
		ItemStack itemStack = new AItemStackBuilder(Material.GOLD_NUGGET)
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"&7Spend &6" + Misc.formatLarge(questLevel.getRequirement(pitPlayer)) + "g &7on helmet abilities",
						"&7(&6Golden Helmet&7) required",
						"",
						"&7Progress: &3" + Misc.formatLarge(progress) + "&7/&3" + Misc.formatLarge(questLevel.getRequirement(pitPlayer)) + " &8[" +
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
	public void createPossibleStates() {
		questLevels.add(new QuestLevel(20_000, 100));
		questLevels.add(new QuestLevel(30_000, 150));
		questLevels.add(new QuestLevel(40_000, 200));
	}

	@Override
	public double getMultiplier(PitPlayer pitPlayer) {
		if(pitPlayer.prestige == 0) return 1;
		return pitPlayer.prestige;
	}
}
