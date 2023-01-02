package dev.kyro.pitsim.adarkzone.old.placeholders;

import dev.kyro.arcticapi.hooks.papi.APAPIPlaceholder;
import dev.kyro.pitsim.adarkzone.old.OldBossManager;
import dev.kyro.pitsim.enums.SubLevel;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Map;

public class SpiderCavePlaceholder implements APAPIPlaceholder {

	@Override
	public String getIdentifier() {
		return "spidercave";
	}

	@Override
	public String getValue(Player player) {
		Map<Player, Integer> players = OldBossManager.bossItems.get(SubLevel.SPIDER_CAVE);
		if(OldBossManager.activePlayers.contains(player)) return "&c&lBOSS SPAWNED!";
		else
			return ChatColor.translateAlternateColorCodes('&', "&a" + players.getOrDefault(player, 0) + "&7/" + SubLevel.SPIDER_CAVE.spawnBossItemCount);
	}
}
