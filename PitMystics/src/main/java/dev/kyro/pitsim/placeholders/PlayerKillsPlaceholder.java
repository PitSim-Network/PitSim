package dev.kyro.pitsim.placeholders;

import dev.kyro.arcticapi.hooks.APAPIPlaceholder;
import dev.kyro.pitsim.controllers.PrestigeValues;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PlayerKillsPlaceholder implements APAPIPlaceholder {

	@Override
	public String getIdentifier() {
		return "playerkills";
	}

	@Override
	public String getValue(Player player) {

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		PrestigeValues.PrestigeInfo prestigeInfo = PrestigeValues.getPrestigeInfo(pitPlayer.prestige);
			return ChatColor.translateAlternateColorCodes('&', "&a" + pitPlayer.playerKills + "&7/" + prestigeInfo.killReq);

	}
}
