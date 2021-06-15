package dev.kyro.pitsim.placeholders;

import dev.kyro.arcticapi.hooks.APAPIPlaceholder;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.entity.Player;

public class SuffixPlaceholder implements APAPIPlaceholder {

	@Override
	public String getIdentifier() {
		return "suffix";
	}

	@Override
	public String getValue(Player player) {

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		return pitPlayer.bounty != 0 ? "&7 &6&l" + pitPlayer.bounty + "g" : "";
	}
}
