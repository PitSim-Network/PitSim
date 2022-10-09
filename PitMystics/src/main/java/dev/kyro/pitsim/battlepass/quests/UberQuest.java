package dev.kyro.pitsim.battlepass.quests;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.battlepass.PassQuest;
import dev.kyro.pitsim.controllers.PlayerManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.events.KillEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class UberQuest extends PassQuest {
	public static UberQuest INSTANCE;

	public UberQuest() {
		super("&d&lProfit!", "uberscompleted", QuestType.WEEKLY);
		INSTANCE = this;
	}

	public void attemptQuestIncrement(PitPlayer pitPlayer) {
		if(!canProgressQuest(pitPlayer)) return;
		progressQuest(pitPlayer, 1);
	}

	@Override
	public ItemStack getDisplayItem(QuestLevel questLevel, double progress) {
		ItemStack itemStack = new AItemStackBuilder(Material.EMERALD)
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"&7Complete &d" + intFormat.format(questLevel.requirement) + " Uberstreaks",
						"",
						"&7Progress: &3" + intFormat.format(progress) + "&7/&3" + intFormat.format(questLevel.requirement) + " &8[" +
								AUtil.createProgressBar("|", ChatColor.AQUA, ChatColor.GRAY, 20, progress / questLevel.requirement) + "&8]",
						"&7Reward: &3" + questLevel.rewardPoints + " &7Quest Points"
				))
				.getItemStack();
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		itemStack.setItemMeta(itemMeta);
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
}
