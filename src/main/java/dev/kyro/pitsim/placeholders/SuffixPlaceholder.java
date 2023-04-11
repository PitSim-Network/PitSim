package dev.kyro.pitsim.placeholders;

import dev.kyro.arcticapi.hooks.papi.APAPIPlaceholder;
import dev.kyro.arcticguilds.GuildData;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.AFKManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.misc.Formatter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SuffixPlaceholder implements APAPIPlaceholder {

	@Override
	public String getIdentifier() {
		return "suffix";
	}

	@Override
	public String getValue(Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		if(AFKManager.AFKPlayers.contains(player)) return "&8 [AFK]";

		if(PitSim.status.isOverworld()) {
			if(pitPlayer.bounty != 0) return "&7 &6&l" + pitPlayer.bounty + "g";
		} else {
			int souls = (int) Math.ceil(KillEvent.getBaseSouls(pitPlayer));
			if(souls != 0) return "&7 &f&l" + ChatColor.stripColor(Formatter.formatSouls(souls));
		}

		GuildData guild = GuildData.getGuildData(player);
		if(guild != null && guild.getTag() != null) return guild.getColor() + " #" + guild.getTag();
		return "";
	}
}
