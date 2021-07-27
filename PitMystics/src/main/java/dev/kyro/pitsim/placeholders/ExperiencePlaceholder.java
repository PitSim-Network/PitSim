package dev.kyro.pitsim.placeholders;

import dev.kyro.arcticapi.hooks.APAPIPlaceholder;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.entity.Player;

public class ExperiencePlaceholder implements APAPIPlaceholder {

	@Override
	public String getIdentifier() {
		return "experience";
	}

	@Override
	public String getValue(Player player) {

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		if(pitPlayer.remainingXP == 0) return "MAXED!";
		else return String.valueOf(pitPlayer.remainingXP);
	}
}
