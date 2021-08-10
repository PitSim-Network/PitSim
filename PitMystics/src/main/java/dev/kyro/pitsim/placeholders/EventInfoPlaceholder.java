package dev.kyro.pitsim.placeholders;

import dev.kyro.arcticapi.hooks.APAPIPlaceholder;
import dev.kyro.pitsim.controllers.PitEventManager;
import dev.kyro.pitsim.pitevents.CaptureTheFlag;
import dev.kyro.pitsim.pitevents.Juggernaut;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Locale;

public class EventInfoPlaceholder implements APAPIPlaceholder {

	@Override
	public String getIdentifier() {
		return "eventinfo";
	}

	@Override
	public String getValue(Player player) {

		if(PitEventManager.majorEvent && PitEventManager.activeEvent.getClass() == Juggernaut.class) {
			if(Juggernaut.juggernaut != player) {
				if(Juggernaut.juggernautDamage.get(player) == null)
					return ChatColor.translateAlternateColorCodes('&', " &7- &6Damage &c0\u2764");
				else
					return ChatColor.translateAlternateColorCodes('&', " &7- &6Damage &c" + Juggernaut.juggernautDamage.get(player) + "&c\u2764");
			} else return ChatColor.translateAlternateColorCodes('&', " &7- &6Kills &a" + Juggernaut.juggernautKills);
		}
		if(PitEventManager.majorEvent && PitEventManager.activeEvent.getClass() == CaptureTheFlag.class) {
			if(CaptureTheFlag.captures.get(player) == null) return ChatColor.translateAlternateColorCodes('&', " &7- &6Captures &e0");
			else return ChatColor.translateAlternateColorCodes('&', " &7- &6Captures &e" + CaptureTheFlag.captures.get(player));
		}
		else return "None";
	}
}
