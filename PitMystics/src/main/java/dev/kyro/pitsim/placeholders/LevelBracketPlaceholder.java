package dev.kyro.pitsim.placeholders;

import dev.kyro.arcticapi.hooks.APAPIPlaceholder;
import dev.kyro.pitsim.controllers.PitPlayer;
import org.bukkit.entity.Player;

public class LevelBracketPlaceholder implements APAPIPlaceholder {

	@Override
	public String getIdentifier() {
		return "level_bracket";
	}

	@Override
	public String getValue(Player player) {

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		return pitPlayer.levelBracket;
	}
}
