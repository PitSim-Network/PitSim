package dev.kyro.pitsim.placeholders;

import dev.kyro.arcticapi.hooks.papi.APAPIPlaceholder;
import dev.kyro.pitsim.controllers.CombatManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CombatTimerPlaceholder implements APAPIPlaceholder {

	@Override
	public String getIdentifier() {
		return "combat";
	}

	@Override
	public String getValue(Player player) {

		Integer time = CombatManager.taggedPlayers.get(player.getUniqueId());
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

		if(pitPlayer.megastreak.isOnMega()) {
			return pitPlayer.megastreak.getPrefix();
		}

		if(time == null) return ChatColor.GREEN + "Idling";
		else if(time / 20 > 9) {
			return ChatColor.RED + "Fighting";
		} else {
			return ChatColor.RED + "Fighting " + ChatColor.GRAY + "(" + (int) Math.ceil(time / 20D) + ")";
		}
	}


}
