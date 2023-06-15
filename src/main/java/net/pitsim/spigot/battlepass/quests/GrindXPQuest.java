package net.pitsim.spigot.battlepass.quests;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AUtil;
import net.pitsim.spigot.battlepass.PassQuest;
import net.pitsim.spigot.controllers.PrestigeValues;
import net.pitsim.spigot.controllers.objects.PitPlayer;
import net.pitsim.spigot.misc.Formatter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class GrindXPQuest extends PassQuest {
	public static GrindXPQuest INSTANCE;

	public GrindXPQuest() {
		super("&b&lEXPert", "grindxp", QuestType.WEEKLY);
		INSTANCE = this;
	}

	public void gainXP(PitPlayer pitPlayer, long xp) {
		progressQuest(pitPlayer, (double) xp);
	}

	@Override
	public ItemStack getDisplayStack(PitPlayer pitPlayer, QuestLevel questLevel, double progress) {
		ItemStack itemStack = new AItemStackBuilder(Material.INK_SACK, 1, 12)
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"&7Grind &b" + Formatter.formatLarge(questLevel.getRequirement(pitPlayer)) + " XP",
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
		questLevels.add(new QuestLevel(25_000.0, 100));
		questLevels.add(new QuestLevel(37_500.0, 150));
		questLevels.add(new QuestLevel(50_000.0, 200));
	}

	@Override
	public double getMultiplier(PitPlayer pitPlayer) {
		return Math.pow(PrestigeValues.getPrestigeInfo(pitPlayer.prestige).getXpMultiplier(), 9.0 / 10.0);
	}
}
