package dev.kyro.pitsim.adarkzone.aaold.placeholders;

import dev.kyro.arcticapi.hooks.papi.APAPIPlaceholder;
import dev.kyro.pitsim.adarkzone.aaold.OldBossManager;
import dev.kyro.pitsim.adarkzone.aaold.OldSubLevel;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Map;

public class EndermanCavePlaceholder implements APAPIPlaceholder {

	@Override
	public String getIdentifier() {
		return "endermancave";
	}

	@Override
	public String getValue(Player player) {
		Map<Player, Integer> players = OldBossManager.bossItems.get(OldSubLevel.ENDERMAN_CAVE);
		if(OldBossManager.activePlayers.contains(player)) return "&c&lBOSS SPAWNED!";
		else
			return ChatColor.translateAlternateColorCodes('&', "&a" + players.getOrDefault(player, 0) + "&7/" + OldSubLevel.ENDERMAN_CAVE.spawnBossItemCount);
	}
}
