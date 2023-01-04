package dev.kyro.pitsim.adarkzone.placeholders;

import dev.kyro.arcticapi.hooks.papi.APAPIPlaceholder;
import dev.kyro.pitsim.adarkzone.DarkzoneManager;
import dev.kyro.pitsim.adarkzone.SubLevel;
import dev.kyro.pitsim.adarkzone.SubLevelType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ZombieCavePlaceholder implements APAPIPlaceholder {
	@Override
	public String getIdentifier() {
		return "zombie_cave";
	}

	@Override
	public String getValue(Player player) {
		SubLevel subLevel = DarkzoneManager.getSublevel(SubLevelType.ZOMBIE);
		if(subLevel == null) return "0";
		if (subLevel.isBossSpawned()) {
			return "&c&lBOSS SPAWNED!";
		} else {
			return ChatColor.translateAlternateColorCodes('&', "&a" + subLevel.getCurrentDrops() + "&7/" + subLevel.getRequiredDropsToSpawn());
		}
	}
}
