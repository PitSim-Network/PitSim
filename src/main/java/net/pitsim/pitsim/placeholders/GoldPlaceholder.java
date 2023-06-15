package net.pitsim.pitsim.placeholders;

import dev.kyro.arcticapi.hooks.papi.APAPIPlaceholder;
import net.pitsim.pitsim.controllers.objects.PitPlayer;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class GoldPlaceholder implements APAPIPlaceholder {

	DecimalFormat formatter = new DecimalFormat("#,###");

	@Override
	public String getIdentifier() {
		return "gold";
	}

	@Override
	public String getValue(Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		return formatter.format(pitPlayer.gold) + "g";
	}
}
