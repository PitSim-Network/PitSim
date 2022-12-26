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

public class AttackBotsWithHealerQuest extends PassQuest {
	public static AttackBotsWithHealerQuest INSTANCE;

	public AttackBotsWithHealerQuest() {
		super("&c&lThey Have Feelings Too", "healbots", QuestType.WEEKLY);
		INSTANCE = this;
	}

	public void healPlayer(PitPlayer pitPlayer, double amount) {
		progressQuest(pitPlayer, amount);
	}

	@Override
	public ItemStack getDisplayItem(PitPlayer pitPlayer, QuestLevel questLevel, double progress) {
		ItemStack itemStack = new AItemStackBuilder(Material.GOLDEN_CARROT)
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"&cHeal &7nons &c" + Misc.getHearts(questLevel.getRequirement(pitPlayer)) + " &7with the",
						"&7enchant &9Healer",
						"",
						"&7Progress: &3" + Misc.formatLarge(progress / 2) + "&7/&3" + Misc.formatLarge(questLevel.getRequirement(pitPlayer) / 2) + " &8[" +
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
		questLevels.add(new QuestLevel(20_000, 150));
		questLevels.add(new QuestLevel(20_000, 200));
		questLevels.add(new QuestLevel(20_000, 250));
	}

	@Override
	public double getMultiplier(PitPlayer pitPlayer) {
		return 1.0;
	}
}
