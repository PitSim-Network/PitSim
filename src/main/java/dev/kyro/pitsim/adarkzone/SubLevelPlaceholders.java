package dev.kyro.pitsim.adarkzone;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SubLevelPlaceholders extends PlaceholderExpansion {

	@Override
	public String onPlaceholderRequest(Player player, @NotNull String identifier) {
		SubLevel subLevel = DarkzoneManager.getSubLevel(identifier);
		if(subLevel == null) return null;
		if (subLevel.isBossSpawned()) {
			return "&c&lBOSS SPAWNED!";
		} else {
			return ChatColor.translateAlternateColorCodes('&', "&a" + subLevel.getCurrentDrops() + "&7/" + subLevel.getRequiredDropsToSpawn());
		}
	}

	@Override
	public @NotNull String getIdentifier() {
		return "sublevel";
	}

	@Override
	public @NotNull String getAuthor() {
		return "KyroKrypt";
	}

	@Override
	public @NotNull String getVersion() {
		return "1.0";
	}
}
