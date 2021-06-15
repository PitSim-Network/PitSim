package dev.kyro.pitsim.placeholders;

import dev.kyro.arcticapi.hooks.APAPIPlaceholder;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.entity.Player;

public class StreakPlaceholder implements APAPIPlaceholder {

	@Override
	public String getIdentifier() {
		return "streak";
	}

	@Override
	public String getValue(Player player) {

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		return pitPlayer.getKills() + "";
	}
}
