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

public class HoursPlayedQuest extends PassQuest {
	public static HoursPlayedQuest INSTANCE;

	public HoursPlayedQuest() {
		super("&e&lMaster Procrastinator", "hoursplayed", QuestType.WEEKLY);
		INSTANCE = this;
	}

	public void progressTime(PitPlayer pitPlayer) {
		progressQuest(pitPlayer, 1);
	}

	@Override
	public ItemStack getDisplayStack(PitPlayer pitPlayer, QuestLevel questLevel, double progress) {
		ItemStack itemStack = new AItemStackBuilder(Material.WATCH)
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"&7Spend &e" + Formatter.formatLarge(questLevel.getRequirement(pitPlayer) / 60) + " &7hours on the",
						"&7server",
						"",
						"&7Progress: &3" + Formatter.formatLarge(progress / 60) + "&7/&3" + Formatter.formatLarge(questLevel.getRequirement(pitPlayer) / 60) + " &8[" +
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
		questLevels.add(new QuestLevel(8 * 60, 150));
		questLevels.add(new QuestLevel(12 * 60, 200));
		questLevels.add(new QuestLevel(16 * 60, 250));
	}

	@Override
	public double getMultiplier(PitPlayer pitPlayer) {
		return 1.0;
	}
}
