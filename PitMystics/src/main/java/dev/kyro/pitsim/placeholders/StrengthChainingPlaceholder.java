package dev.kyro.pitsim.placeholders;

import dev.kyro.arcticapi.hooks.papi.APAPIPlaceholder;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.perks.StrengthChaining;
import org.bukkit.entity.Player;

public class StrengthChainingPlaceholder implements APAPIPlaceholder {

	@Override
	public String getIdentifier() {
		return "strength_level";
	}

	@Override
	public String getValue(Player player) {

		Integer level = StrengthChaining.amplifierMap.getOrDefault(player.getUniqueId(), 0);
		Integer time = StrengthChaining.timerMap.getOrDefault(player.getUniqueId(), 0);
		if(level == null || level == 0) return "None";

		return "&c" + AUtil.toRoman(level) + " &7(" + getTime(time) + ")";
	}

	public int getTime(int time) {

		return (int) Math.ceil(time / 20D);
	}
}
