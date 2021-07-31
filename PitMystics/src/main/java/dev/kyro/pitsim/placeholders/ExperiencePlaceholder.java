package dev.kyro.pitsim.placeholders;

import dev.kyro.arcticapi.hooks.APAPIPlaceholder;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.Locale;

public class ExperiencePlaceholder implements APAPIPlaceholder {

	@Override
	public String getIdentifier() {
		return "experience";
	}

	@Override
	public String getValue(Player player) {

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		if(pitPlayer.remainingXP == 0) return "MAXED!";
		else return (NumberFormat.getNumberInstance(Locale.US).format(pitPlayer.remainingXP));
	}
}
