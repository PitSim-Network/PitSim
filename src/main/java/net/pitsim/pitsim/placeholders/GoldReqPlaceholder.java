package net.pitsim.pitsim.placeholders;

import dev.kyro.arcticapi.hooks.papi.APAPIPlaceholder;
import net.pitsim.pitsim.controllers.PrestigeValues;
import net.pitsim.pitsim.controllers.objects.PitPlayer;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.Locale;

public class GoldReqPlaceholder implements APAPIPlaceholder {

	@Override
	public String getIdentifier() {
		return "goldreq";
	}

	@Override
	public String getValue(Player player) {

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		PrestigeValues.PrestigeInfo prestigeInfo = PrestigeValues.getPrestigeInfo(pitPlayer.prestige);
		double goldReq = prestigeInfo.getGoldReq();

		if(goldReq - pitPlayer.goldGrinded <= 0) return "&aDONE!";
		else return (NumberFormat.getNumberInstance(Locale.US).format(goldReq - pitPlayer.goldGrinded)) + "g";
	}
}
