package net.pitsim.spigot.placeholders;

import dev.kyro.arcticapi.hooks.papi.APAPIPlaceholder;
import dev.kyro.arcticapi.misc.AUtil;
import net.pitsim.spigot.controllers.PostPrestigeManager;
import net.pitsim.spigot.controllers.PrestigeValues;
import net.pitsim.spigot.controllers.objects.PitPlayer;
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
		builder.append(prestigeInfo.getBracketColor());
		builder.append("-");
		builder.append(PrestigeValues.getLevelColor(pitPlayer.level)).append(pitPlayer.level);
		builder.append(prestigeInfo.getCloseBracket());

		String star = PostPrestigeManager.getStarString(player);
		if(!star.isEmpty()) builder.append(" ").append(star);

		builder.append(" ");
		return builder.toString();
	}
}
