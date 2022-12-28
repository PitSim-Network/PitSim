package dev.kyro.pitsim.placeholders;

import dev.kyro.arcticapi.hooks.papi.APAPIPlaceholder;
import dev.kyro.arcticguilds.GuildLeaderboardData;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class GuildPlaceholder8 implements APAPIPlaceholder {

	@Override
	public String getIdentifier() {
		return "guild8";
	}

	@Override
	public String getValue(Player player) {
		GuildLeaderboardData guild = GuildLeaderboardData.getGuildData(7);
		if(guild == null) return "&cNone!";
		DecimalFormat formatter = new DecimalFormat("#,###.#");
		StringBuilder string = new StringBuilder("&78. ");
		string.append(guild.getColor()).append(guild.name).append(" &7- &e").append(formatter.format(guild.reputation));
		return ChatColor.translateAlternateColorCodes('&', string.toString());
	}
}
