package dev.kyro.pitsim.battlepass.quests;

import dev.kyro.pitsim.battlepass.PassQuest;
import dev.kyro.pitsim.events.KillEvent;
import org.bukkit.event.EventHandler;

import java.util.List;

public class BotKillQuest extends PassQuest {

	@EventHandler
	public void onKill(KillEvent killEvent) {

	}

	@Override
	public List<QuestLevel> getPossibleStates() {
		return null;
	}
}
