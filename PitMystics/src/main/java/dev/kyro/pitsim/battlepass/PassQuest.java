package dev.kyro.pitsim.battlepass;

import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.List;

public abstract class PassQuest implements Listener {
	public QuestType questType;

//	Only applies to weekly quests
	public abstract List<QuestLevel> getPossibleStates();

	public boolean hasQuest(Player player) {
		if(Bukkit.getOnlinePlayers().contains(player)) return false;
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

		return true;
	}

	public static class QuestLevel {
		public int requirement;
		public int rewardPoints;
	}

	public enum QuestType {
		DAILY,
		WEEKLY
	}
}
