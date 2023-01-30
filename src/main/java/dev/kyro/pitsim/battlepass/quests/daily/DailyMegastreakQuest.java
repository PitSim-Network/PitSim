package dev.kyro.pitsim.battlepass.quests.daily;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.battlepass.PassQuest;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class DailyMegastreakQuest extends PassQuest {
	public static DailyMegastreakQuest INSTANCE;

	public DailyMegastreakQuest() {
		super("&c&lKill Bots", "dailybotkills", QuestType.DAILY);
		INSTANCE = this;
	}

	public void onMegastreakComplete(PitPlayer pitPlayer) {
		progressQuest(pitPlayer, 1);
	}

	@Override
	public ItemStack getDisplayItem(PitPlayer pitPlayer, QuestLevel questLevel, double progress) {
		ItemStack itemStack = new AItemStackBuilder(Material.DIAMOND)
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"&7Complete &e" + Misc.formatLarge(questLevel.getRequirement(pitPlayer)) + " &7megastreaks",
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
	public void createPossibleStates() {
		questLevels.add(new QuestLevel(1, 40));
	}

	@Override
	public double getMultiplier(PitPlayer pitPlayer) {
		return 1.0;
	}
}
