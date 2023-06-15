package net.pitsim.spigot.placeholders;

import dev.kyro.arcticapi.hooks.papi.APAPIPlaceholder;
import net.pitsim.spigot.controllers.objects.PitPlayer;
import org.bukkit.entity.Player;

public class PrefixPlaceholder implements APAPIPlaceholder {

	@Override
	public String getIdentifier() {
		return "prefix";
	}

	@Override
	public String getValue(Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		return pitPlayer.getPrefix();
	}
}
