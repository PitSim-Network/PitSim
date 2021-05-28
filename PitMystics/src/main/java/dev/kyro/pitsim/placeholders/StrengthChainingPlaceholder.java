package dev.kyro.pitsim.placeholders;

import dev.kyro.arcticapi.hooks.APAPIPlaceholder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.controllers.PitPlayer;
import dev.kyro.pitsim.upgrades.StrengthChaining;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class StrengthChainingPlaceholder implements APAPIPlaceholder {

	@Override
	public String getIdentifier() {
		return "strength_level";
	}

	@Override
	public String getValue(Player player) {

		int level = StrengthChaining.strength.get(player);
		int time = StrengthChaining.timer.get(player);

		String output = ChatColor.translateAlternateColorCodes('&', "&c" + AUtil.toRoman(level) + " &7(" + time + ")");

		if(level == 0 ) return null;
		else return output;
	}


}
