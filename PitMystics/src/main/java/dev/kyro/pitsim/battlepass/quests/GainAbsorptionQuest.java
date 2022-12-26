package dev.kyro.pitsim.battlepass.quests;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.battlepass.PassQuest;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.events.HealEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GainAbsorptionQuest extends PassQuest {
	public static GainAbsorptionQuest INSTANCE;

	public GainAbsorptionQuest() {
		super("&6&lFat", "gainabsorption", QuestType.WEEKLY);
		INSTANCE = this;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onHeal(HealEvent healEvent) {
		if(healEvent.pitEnchant == null) return;
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(healEvent.player);
		progressQuest(pitPlayer, healEvent.getEffectiveHeal());
	}

	@Override
	public ItemStack getDisplayItem(PitPlayer pitPlayer, QuestLevel questLevel, double progress) {
		ItemStack itemStack = new AItemStackBuilder(Material.GOLDEN_APPLE)
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"&7Gain &6" + Misc.getHearts(questLevel.getRequirement(pitPlayer)) + " &7of absorption",
						"&7from enchants",
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
		questLevels.add(new QuestLevel(20_000, 100));
		questLevels.add(new QuestLevel(30_000, 150));
		questLevels.add(new QuestLevel(40_000, 200));
	}

	@Override
	public double getMultiplier(PitPlayer pitPlayer) {
		return 1.0;
	}
}
