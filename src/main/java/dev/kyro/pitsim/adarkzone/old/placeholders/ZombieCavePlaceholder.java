package dev.kyro.pitsim.adarkzone.old.placeholders;

import dev.kyro.arcticapi.hooks.papi.APAPIPlaceholder;
import dev.kyro.pitsim.adarkzone.BossManager;
import dev.kyro.pitsim.enums.SubLevel;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Map;

public class ZombieCavePlaceholder implements APAPIPlaceholder {

	@Override
	public String getIdentifier() {
		return "zombiecave";
	}

	@Override
	public String getValue(Player player) {
		Map<Player, Integer> players = BossManager.bossItems.get(SubLevel.ZOMBIE_CAVE);
		if(BossManager.activePlayers.contains(player)) return "&c&lBOSS SPAWNED!";
		else
			return ChatColor.translateAlternateColorCodes('&', "&a" + players.getOrDefault(player, 0) + "&7/" + SubLevel.ZOMBIE_CAVE.spawnBossItemCount);
	}
}
