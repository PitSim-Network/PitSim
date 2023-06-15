package net.pitsim.pitsim.placeholders;

import dev.kyro.arcticapi.hooks.papi.APAPIPlaceholder;
import dev.kyro.arcticapi.misc.AUtil;
import net.pitsim.pitsim.controllers.objects.PitPlayer;
import org.bukkit.entity.Player;

public class PrestigePlaceholder implements APAPIPlaceholder {

	@Override
	public String getIdentifier() {
		return "prestige";
	}

	@Override
	public String getValue(Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		return pitPlayer.prestige == 0 ? "0" : AUtil.toRoman(pitPlayer.prestige);
	}
}
