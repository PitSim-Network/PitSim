package dev.kyro.pitsim.battlepass.quests;

import dev.kyro.pitsim.battlepass.PassQuest;
import dev.kyro.pitsim.events.KillEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class BotKillQuest extends PassQuest {

	public BotKillQuest() {
		super("Bot kills", "botkills", QuestType.DAILY);
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(!killEvent.killerIsPlayer || !killEvent.deadIsPlayer) return;
//		if(!canProgressQuest(killEvent.killerPlayer) || NonManager.getNon(killEvent.dead) == null) return;
	}

	@Override
	public ItemStack getDisplayItem(QuestLevel questLevel, double progression) {
		return null;
	}

	@Override
	public List<QuestLevel> getPossibleStates() {
		return null;
	}
}
