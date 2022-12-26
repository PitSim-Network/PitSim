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

public class WinAuctionsQuest extends PassQuest {
	public static WinAuctionsQuest INSTANCE;

	public WinAuctionsQuest() {
		super("&f&lMoneybags", "winauctions", QuestType.WEEKLY);
		INSTANCE = this;
	}

	public void winAuction(PitPlayer pitPlayer) {
		progressQuest(pitPlayer, 1);
	}

	@Override
	public ItemStack getDisplayItem(PitPlayer pitPlayer, QuestLevel questLevel, double progress) {
		ItemStack itemStack = new AItemStackBuilder(Material.DIAMOND)
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"&7Win &f" + Misc.formatLarge(questLevel.getRequirement(pitPlayer)) + " &7action" +
								(questLevel.getRequirement(pitPlayer) == 1 ? "" : "s") + " (found in",
						"&7the &5darkzone&7)",
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
	public void createWeeklyPossibleStates() {
		questLevels.add(new QuestLevel(1, 100));
		questLevels.add(new QuestLevel(2, 150));
		questLevels.add(new QuestLevel(3, 200));
	}

	@Override
	public double getMultiplier(PitPlayer pitPlayer) {
		return 1;
	}
}
