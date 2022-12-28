package dev.kyro.pitsim.placeholders;

import dev.kyro.arcticapi.hooks.papi.APAPIPlaceholder;
import dev.kyro.pitsim.controllers.PrestigeValues;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.Locale;

public class SoulReqPlaceholder implements APAPIPlaceholder {

	@Override
	public String getIdentifier() {
		return "soulreq";
	}

	@Override
	public String getValue(Player player) {

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		PrestigeValues.PrestigeInfo prestigeInfo = PrestigeValues.getPrestigeInfo(pitPlayer.prestige);
		double soulReq = prestigeInfo.soulReq;


		if(soulReq - pitPlayer.soulsGathered <= 0) return "&aDONE!";
		else
			return ChatColor.WHITE + (NumberFormat.getNumberInstance(Locale.US).format(soulReq - pitPlayer.soulsGathered));
	}
}
