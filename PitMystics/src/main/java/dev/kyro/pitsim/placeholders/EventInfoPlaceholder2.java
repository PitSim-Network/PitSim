package dev.kyro.pitsim.placeholders;

import dev.kyro.arcticapi.hooks.APAPIPlaceholder;
import dev.kyro.pitsim.controllers.PitEventManager;
import dev.kyro.pitsim.pitevents.CaptureTheFlag;
import dev.kyro.pitsim.pitevents.Juggernaut;
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

		if(PitEventManager.majorEvent && PitEventManager.activeEvent.getClass() == Juggernaut.class) {
			return ChatColor.translateAlternateColorCodes('&',
					" &7- &6Health &c" + (int) Juggernaut.juggernaut.getHealth() / 2+ "&7/&c" + (int) Juggernaut.juggernaut.getMaxHealth() / 2  + "&c\u2764");
		}

		if(PitEventManager.majorEvent && PitEventManager.activeEvent.getClass() == CaptureTheFlag.class) {
			if(CaptureTheFlag.red.contains(player)) return ChatColor.translateAlternateColorCodes('&',
					" &7- &6Score &c" + CaptureTheFlag.getRedScore() + " &6vs &9" + CaptureTheFlag.getBlueScore());
			else return ChatColor.translateAlternateColorCodes('&',
					" &7- &6Score &9" + CaptureTheFlag.getBlueScore() + " &6vs &c" + CaptureTheFlag.getRedScore());
		}
		return "None";
	}
}