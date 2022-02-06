package dev.kyro.pitsim.placeholders;

import dev.kyro.arcticapi.hooks.APAPIPlaceholder;
import dev.kyro.arcticguilds.controllers.objects.Guild;
import dev.kyro.pitsim.controllers.GuildLeaderboard;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class GuildPlaceholder3 implements APAPIPlaceholder {

	@Override
	public String getIdentifier() {
		return "guild3";
	}

	@Override
	public String getValue(Player player) {
		if(GuildLeaderboard.topGuilds.size() < 3) return "&cNone!";
		Guild guild = GuildLeaderboard.topGuilds.get(2);
		DecimalFormat formatter = new DecimalFormat("#,###.#");
		StringBuilder string = new StringBuilder("&63. ");
		string.append(guild.getColor()).append(guild.name).append(" &7- &e").append(formatter.format(guild.reputation));
		return ChatColor.translateAlternateColorCodes('&', string.toString());
	}
}
