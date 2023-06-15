package net.pitsim.spigot.placeholders;

import dev.kyro.arcticapi.hooks.papi.APAPIPlaceholder;
import net.pitsim.spigot.controllers.objects.PitPlayer;
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
