package dev.kyro.pitsim.placeholders;

import dev.kyro.arcticapi.hooks.APAPIPlaceholder;
import dev.kyro.pitsim.controllers.PitEventManager;
import dev.kyro.pitsim.pitevents.CaptureTheFlag;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Locale;

public class EventInfoPlaceholder2 implements APAPIPlaceholder {

	@Override
	public String getIdentifier() {
		return "eventinfo2";
	}

	@Override
	public String getValue(Player player) {

		if(PitEventManager.majorEvent && PitEventManager.activeEvent.getClass() == CaptureTheFlag.class) {
			if(CaptureTheFlag.red.contains(player)) return ChatColor.translateAlternateColorCodes('&',
					" &7- &6Score &c" + CaptureTheFlag.getRedScore() + " &6vs &9" + CaptureTheFlag.getBlueScore());
			else return ChatColor.translateAlternateColorCodes('&',
					" &7- &6Score &9" + CaptureTheFlag.getBlueScore() + " &6vs &c" + CaptureTheFlag.getRedScore());
		}
		else return "None";
	}
}