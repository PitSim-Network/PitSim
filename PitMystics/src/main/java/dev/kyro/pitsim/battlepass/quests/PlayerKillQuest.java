package dev.kyro.pitsim.battlepass.quests;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.battlepass.PassQuest;
import dev.kyro.pitsim.controllers.PlayerManager;
import dev.kyro.pitsim.events.KillEvent;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class PlayerKillQuest extends PassQuest {

	public PlayerKillQuest() {
		super("Player Kills", "playerkills", QuestType.WEEKLY);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onKill(KillEvent killEvent) {
		if(!killEvent.isKillerPlayer() || !killEvent.isDeadPlayer()) return;
		if(!PlayerManager.isRealPlayer(killEvent.getKillerPlayer()) || !canProgressQuest(killEvent.getKillerPitPlayer())
				|| !PlayerManager.isRealPlayer(killEvent.getDeadPlayer())) return;

		progressQuest(killEvent.getKillerPitPlayer(), 1);
	}

	@Override
	public ItemStack getDisplayItem(QuestLevel questLevel, double progression) {
		DecimalFormat decimalFormat = new DecimalFormat("0.#");
		ItemStack itemStack = new AItemStackBuilder(Material.DIAMOND_SWORD)
				.setName("&b&l" + displayName)
				.setLore(new ALoreBuilder(
						"&7Kill 30 player",
						"&7Progress: " + decimalFormat.format(progression) + "/" + decimalFormat.format(questLevel.requirement)
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
