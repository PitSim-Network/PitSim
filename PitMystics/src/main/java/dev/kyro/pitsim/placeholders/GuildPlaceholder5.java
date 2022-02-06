package dev.kyro.pitsim.placeholders;

import dev.kyro.arcticapi.hooks.APAPIPlaceholder;
import dev.kyro.arcticguilds.controllers.objects.Guild;
import dev.kyro.pitsim.controllers.GuildLeaderboard;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class GuildPlaceholder5 implements APAPIPlaceholder {

	@Override
	public String getIdentifier() {
		return "guild5";
	}

	@Override
	public String getValue(Player player) {
		if(GuildLeaderboard.topGuilds.get(0) == null) return "&cNone!";
		Guild guild = GuildLeaderboard.topGuilds.get(4);

		StringBuilder string = new StringBuilder("&75. ");
		string.append(guild.getColor()).append(guild.name).append( "&7- &e").append(guild.getRepPoints());
		return ChatColor.translateAlternateColorCodes('&', string.toString());
	}
}
