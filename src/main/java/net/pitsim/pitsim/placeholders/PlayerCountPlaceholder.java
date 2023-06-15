package net.pitsim.pitsim.placeholders;

import dev.kyro.arcticapi.hooks.papi.APAPIPlaceholder;
import net.pitsim.pitsim.controllers.ProxyMessaging;
import org.bukkit.entity.Player;

public class PlayerCountPlaceholder implements APAPIPlaceholder {

	@Override
	public String getIdentifier() {
		return "players";
	}

	@Override
	public String getValue(Player player) {
		return Integer.toString(ProxyMessaging.playersOnline);
	}
}
