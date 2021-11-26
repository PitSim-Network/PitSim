package dev.kyro.pitsim.placeholders;

import dev.kyro.arcticapi.hooks.APAPIPlaceholder;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

public class PrefixPlaceholder implements APAPIPlaceholder {

	@Override
	public String getIdentifier() {
		return "prefix";
	}

	@Override
	public String getValue(Player player) {

		String message = "%luckperms_prefix%";

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		return pitPlayer.prefix + PlaceholderAPI.setPlaceholders(player, message);
	}
}
