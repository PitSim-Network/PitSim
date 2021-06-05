package dev.kyro.pitsim.placeholders;

import dev.kyro.arcticapi.hooks.APAPIPlaceholder;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.perks.Gladiator;
import dev.kyro.pitsim.perks.StrengthChaining;
import org.bukkit.entity.Player;

public class GladiatorPlaceholder implements APAPIPlaceholder {

	@Override
	public String getIdentifier() {
		return "gladiator_reduction";
	}

	@Override
	public String getValue(Player player) {

		Integer reduction = Gladiator.amplifierMap.get(player.getUniqueId());
		if(reduction == null || reduction == 0 || !Gladiator.INSTANCE.hasPerk(player)) return "None";

		return "&9-" + reduction * 3 + "&9%";
	}


}
