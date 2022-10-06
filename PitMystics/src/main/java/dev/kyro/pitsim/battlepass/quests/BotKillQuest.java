package dev.kyro.pitsim.battlepass.quests;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.battlepass.PassQuest;
import dev.kyro.pitsim.controllers.NonManager;
import dev.kyro.pitsim.controllers.PlayerManager;
import dev.kyro.pitsim.events.KillEvent;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.List;

public class BotKillQuest extends PassQuest {

	public BotKillQuest() {
		super("Bot Kills", "botkills", QuestType.DAILY);
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(!killEvent.isKillerPlayer() || !killEvent.isDeadPlayer()) return;
		if(!PlayerManager.isRealPlayer(killEvent.getKillerPlayer()) || !canProgressQuest(killEvent.getKillerPitPlayer())
				|| NonManager.getNon(killEvent.getDead()) == null) return;

		progressQuest(killEvent.getKillerPitPlayer(), 1);
	}

	@Override
	public ItemStack getDisplayItem(QuestLevel questLevel, double progress) {
		DecimalFormat decimalFormat = new DecimalFormat("0.#");
		ItemStack itemStack = new AItemStackBuilder(Material.DIAMOND_SWORD)
				.setName("&b&l" + getDisplayName())
				.setLore(new ALoreBuilder(
						"&7Kill 30 bots",
						"&7Progress: " + decimalFormat.format(progress) + "/" + decimalFormat.format(questLevel.requirement)
				))
				.getItemStack();
		return itemStack;
	}

	@Override
	public QuestLevel getDailyState() {
		return new QuestLevel(30.0, 100);
	}

	@Override
	public List<QuestLevel> getWeeklyPossibleStates() {
		return null;
	}
}
