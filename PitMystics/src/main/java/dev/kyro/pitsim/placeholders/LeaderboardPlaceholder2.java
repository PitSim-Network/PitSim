package dev.kyro.pitsim.placeholders;

import dev.kyro.arcticapi.hooks.APAPIPlaceholder;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.controllers.NewLeaderboardManager;
import dev.kyro.pitsim.controllers.PrestigeValues;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class LeaderboardPlaceholder2 implements APAPIPlaceholder {

	@Override
	public String getIdentifier() {
		return "leader2";
	}

	@Override
	public String getValue(Player player) {

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

		FileConfiguration key = (FileConfiguration) NewLeaderboardManager.sortedMap.keySet().toArray()[NewLeaderboardManager.sortedMap.size() - 2];
		int value = NewLeaderboardManager.sortedMap.get(key);

		StringBuilder levelBuilder = new StringBuilder();
		PrestigeValues.PrestigeInfo info = PrestigeValues.getPrestigeInfo(key.getInt("prestige"));
		levelBuilder.append(info.getOpenBracket()).append(ChatColor.YELLOW).append(AUtil.toRoman(key.getInt("prestige")))
				.append(ChatColor.YELLOW + "-").append(PrestigeValues.getLevelColor(key.getInt("level"))).append(key.getString("level")).append(info.getCloseBracket());

		StringBuilder builder = new StringBuilder();
		builder.append(ChatColor.GOLD).append(key.getString("name")).append(" " + ChatColor.GRAY + "- ");

		return ChatColor.translateAlternateColorCodes('&', "&72. " + builder.toString() + levelBuilder.toString());
	}
}
