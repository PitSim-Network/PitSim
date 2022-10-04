package dev.kyro.pitsim.battlepass.quests;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.battlepass.PassQuest;
import dev.kyro.pitsim.events.KillEvent;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class BotKillQuest extends PassQuest {

	public BotKillQuest() {
		super("Bot Kills", "botkills", QuestType.DAILY);
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(!killEvent.isKillerPlayer() || !killEvent.isDeadPlayer()) return;
//		if(!canProgressQuest(killEvent.killerPlayer) || NonManager.getNon(killEvent.dead) == null) return;
	}

	@Override
	public ItemStack getDisplayItem(QuestLevel questLevel, double progression) {
		ItemStack itemStack = new AItemStackBuilder(Material.DIAMOND_SWORD)
				.setName("&b&l" + displayName)
				.setLore(new ALoreBuilder(
						"&7Kill 30 bots"
				))
				.getItemStack();
		return itemStack;
	}

	@Override
	public List<QuestLevel> getPossibleStates() {
		return null;
	}
}
