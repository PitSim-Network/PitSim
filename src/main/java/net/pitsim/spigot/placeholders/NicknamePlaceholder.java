package net.pitsim.spigot.placeholders;

import dev.kyro.arcticapi.hooks.papi.APAPIPlaceholder;
import net.pitsim.spigot.controllers.objects.PitPlayer;
import org.bukkit.entity.Player;

public class NicknamePlaceholder implements APAPIPlaceholder {

	@Override
	public String getIdentifier() {
		return "nickname";
	}

	@Override
	public String getValue(Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		if(pitPlayer.nickname == null || !player.hasPermission("pitsim.nick")) return player.getName();
		return pitPlayer.nickname;
	}
}
