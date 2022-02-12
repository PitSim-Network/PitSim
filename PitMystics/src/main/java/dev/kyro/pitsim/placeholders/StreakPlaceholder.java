package dev.kyro.pitsim.placeholders;

import dev.kyro.arcticapi.hooks.papi.APAPIPlaceholder;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.perks.AssistantToTheStreaker;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class StreakPlaceholder implements APAPIPlaceholder {

	@Override
	public String getIdentifier() {
		return "streak";
	}

	@Override
	public String getValue(Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

		if(pitPlayer.hasPerk(AssistantToTheStreaker.INSTANCE) && pitPlayer.getKills() != 0) {
			double assistantKills = pitPlayer.getKills() + pitPlayer.assistAmount;
			DecimalFormat df = new DecimalFormat("#.#");
			return df.format(assistantKills);
		} else {
			int kills = (int) Math.floor(pitPlayer.getKills());
			return String.valueOf(kills);
		}
	}
}
