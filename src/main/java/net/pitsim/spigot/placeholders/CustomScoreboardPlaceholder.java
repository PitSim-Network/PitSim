package net.pitsim.spigot.placeholders;

import dev.kyro.arcticapi.hooks.papi.APAPIPlaceholder;
import net.pitsim.spigot.controllers.ScoreboardManager;
import net.pitsim.spigot.controllers.objects.PitPlayer;
import net.pitsim.spigot.settings.scoreboard.ScoreboardOption;
import org.bukkit.entity.Player;

public class CustomScoreboardPlaceholder implements APAPIPlaceholder {

	@Override
	public String getIdentifier() {
		return "custom_scoreboard";
	}

	@Override
	public String getValue(Player player) {
		if(!player.hasPermission("pitsim.scoreboard")) return null;
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		for(String refName :pitPlayer.scoreboardData.getPriorityList()) {
			if(!pitPlayer.scoreboardData.getStatusMap().get(refName)) continue;
			ScoreboardOption scoreboardOption = ScoreboardManager.getScoreboardOption(refName);
			String value = scoreboardOption.getValue(pitPlayer);
			if(value == null) continue;
			return value;
		}
		return "&6Custom: &eNothing!";
	}
}
