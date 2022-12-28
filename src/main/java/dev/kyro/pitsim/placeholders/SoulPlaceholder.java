package dev.kyro.pitsim.placeholders;

import dev.kyro.arcticapi.hooks.papi.APAPIPlaceholder;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.Locale;

public class SoulPlaceholder implements APAPIPlaceholder {

	@Override
	public String getIdentifier() {
		return "souls";
	}

	@Override
	public String getValue(Player player) {

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		return ChatColor.WHITE + (NumberFormat.getNumberInstance(Locale.US).format(pitPlayer.taintedSouls));
	}
}
