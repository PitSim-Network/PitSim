package net.pitsim.pitsim.battlepass.quests;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AUtil;
import net.pitsim.pitsim.battlepass.PassQuest;
import net.pitsim.pitsim.controllers.objects.PitPlayer;
import net.pitsim.pitsim.misc.Formatter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class EarnGuildReputationQuest extends PassQuest {
	public static EarnGuildReputationQuest INSTANCE;

	public EarnGuildReputationQuest() {
		super("&a&lTeam Carry", "earnguildrep", QuestType.WEEKLY);
		INSTANCE = this;
	}

	public void gainReputation(PitPlayer pitPlayer, int amount) {
		progressQuest(pitPlayer, amount);
	}

	@Override
	public ItemStack getDisplayStack(PitPlayer pitPlayer, QuestLevel questLevel, double progress) {
		ItemStack itemStack = new AItemStackBuilder(Material.BANNER, 1, 15)
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"&7Earn &a" + Formatter.formatLarge(questLevel.getRequirement(pitPlayer)) + " &7reputation points",
						"&7for your &aguild",
						"",
						"&7Progress: &3" + Formatter.formatLarge(progress) + "&7/&3" + Formatter.formatLarge(questLevel.getRequirement(pitPlayer)) + " &8[" +
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
		questLevels.add(new QuestLevel(30_000, 100));
		questLevels.add(new QuestLevel(45_000, 150));
		questLevels.add(new QuestLevel(60_000, 200));
	}

	@Override
	public double getMultiplier(PitPlayer pitPlayer) {
		return 1;
	}
}
