package dev.kyro.pitsim.battlepass.quests;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.battlepass.PassQuest;
import dev.kyro.pitsim.controllers.NonManager;
import dev.kyro.pitsim.controllers.PlayerManager;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class DoTrueDamageVSBotsQuest extends PassQuest {

	public DoTrueDamageVSBotsQuest() {
		super("&9&lMenace Streaking", "truedamagebots", QuestType.WEEKLY);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onKill(AttackEvent.Apply attackEvent) {
		if(!PlayerManager.isRealPlayer(attackEvent.getAttackerPlayer()) || !canProgressQuest(attackEvent.getAttackerPitPlayer())
				|| NonManager.getNon(attackEvent.getDefender()) == null) return;
		progressQuest(attackEvent.getAttackerPitPlayer(), attackEvent.trueDamage);
	}

	@Override
	public ItemStack getDisplayItem(QuestLevel questLevel, double progress) {
		ItemStack itemStack = new AItemStackBuilder(Material.DAYLIGHT_DETECTOR)
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"&7Deal &9" + Misc.getHearts(questLevel.requirement) + " &7of true damage to",
						"&7bots",
						"",
						"&7Progress: &3" + intFormat.format(progress / 2) + "&7/&3" + intFormat.format(questLevel.requirement / 2) + " &8[" +
								AUtil.createProgressBar("|", ChatColor.AQUA, ChatColor.GRAY, 20, progress / questLevel.requirement) + "&8]",
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
	public List<QuestLevel> getWeeklyPossibleStates() {
		List<QuestLevel> questLevels = new ArrayList<>();
		questLevels.add(new QuestLevel(30.0, 100));
		return questLevels;
	}
}
