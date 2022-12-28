package dev.kyro.pitsim.placeholders;

import dev.kyro.arcticapi.hooks.papi.APAPIPlaceholder;
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

		int kills = pitPlayer.getKills();
		return String.valueOf(kills);
	}
}
