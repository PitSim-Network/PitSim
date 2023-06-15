package net.pitsim.pitsim.placeholders;

import dev.kyro.arcticapi.hooks.papi.APAPIPlaceholder;
import net.pitsim.pitsim.controllers.PrestigeValues;
import net.pitsim.pitsim.controllers.objects.PitPlayer;
import org.bukkit.entity.Player;

public class LevelPlaceholder implements APAPIPlaceholder {

	@Override
	public String getIdentifier() {
		return "level";
	}

	@Override
	public String getValue(Player player) {

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		return PrestigeValues.getPlayerPrefix(player);
	}
}
