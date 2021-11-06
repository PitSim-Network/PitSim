package dev.kyro.pitsim.placeholders;

import dev.kyro.arcticapi.hooks.APAPIPlaceholder;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.controllers.PrestigeValues;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PrestigeLevelPlaceholder implements APAPIPlaceholder {

	@Override
	public String getIdentifier() {
		return "prestigelevel";
	}

	@Override
	public String getValue(Player player) {


		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

		if(pitPlayer.prestige == 0) return PrestigeValues.getPlayerPrefixNameTag(player);

		StringBuilder builder = new StringBuilder();
		PrestigeValues.PrestigeInfo prestigeInfo = PrestigeValues.getPrestigeInfo(pitPlayer.prestige);
		builder.append(prestigeInfo.getOpenBracket());
		builder.append(ChatColor.YELLOW).append(AUtil.toRoman(pitPlayer.prestige));
		if(pitPlayer.prestige == 50) builder.append(ChatColor.WHITE);
	    else builder.append(prestigeInfo.bracketColor);
		builder.append("-");
		builder.append(PrestigeValues.getLevelColor(pitPlayer.level)).append(pitPlayer.level);
		builder.append(prestigeInfo.getCloseBracket());
		builder.append(" ");
		return builder.toString();

	}
}
