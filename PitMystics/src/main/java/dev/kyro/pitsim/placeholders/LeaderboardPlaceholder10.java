package dev.kyro.pitsim.placeholders;

import dev.kyro.arcticapi.hooks.APAPIPlaceholder;
import dev.kyro.pitsim.controllers.LeaderboardManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class LeaderboardPlaceholder10 implements APAPIPlaceholder {

	@Override
	public String getIdentifier() {
		return "leader10";
	}

	@Override
	public String getValue(Player player) {

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

		String key = (String) LeaderboardManager.finalSorted.keySet().toArray()[9];
		int value = LeaderboardManager.finalLevels.get(key);

		return ChatColor.translateAlternateColorCodes('&', "&710. &6" + key + " &7- &e" + value);
	}
}
