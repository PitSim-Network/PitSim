package dev.kyro.pitsim.placeholders;

import dev.kyro.arcticapi.hooks.papi.APAPIPlaceholder;
import dev.kyro.pitsim.controllers.ScoreboardManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.settings.scoreboard.ScoreboardOption;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class CustomScoreboardPlaceholder implements APAPIPlaceholder {

	@Override
	public String getIdentifier() {
		return "custom_scoreboard";
	}

	@Override
	public String getValue(Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		for(String refName : new ArrayList<>(pitPlayer.scoreboardData.getPriorityList())) {
			if(!pitPlayer.scoreboardData.getStatusMap().get(refName)) continue;
			ScoreboardOption scoreboardOption = ScoreboardManager.getScoreboardOption(refName);
			if(scoreboardOption == null) {
				pitPlayer.scoreboardData.getPriorityList().remove(refName);
				pitPlayer.scoreboardData.getStatusMap().remove(refName);
				continue;
			}
			String value = scoreboardOption.getValue(pitPlayer);
			if(value == null) continue;
			return value;
		}
		return "";
	}
}
