package dev.kyro.pitsim.placeholders;

import dev.kyro.arcticapi.hooks.APAPIPlaceholder;
import dev.kyro.pitsim.controllers.PitEventManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Locale;

public class EventNamePlaceholder implements APAPIPlaceholder {

	@Override
	public String getIdentifier() {
		return "event";
	}

	@Override
	public String getValue(Player player) {


		if(PitEventManager.majorEvent) {
			if(PitEventManager.activeEvent.abreviatedName != null) {
				return PitEventManager.activeEvent.color + "" + ChatColor.BOLD + PitEventManager.activeEvent.abreviatedName.toUpperCase(Locale.ROOT);
			}
			else return "None";
		}
		else return "None";
	}
}
