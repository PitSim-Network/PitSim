package dev.kyro.pitsim.battlepass.rewards;

import dev.kyro.pitsim.battlepass.PassReward;
import dev.kyro.pitsim.controllers.LevelManager;
import org.bukkit.entity.Player;

public class PassXpReward extends PassReward {
	public long xp;

	public PassXpReward(long xp) {
		this.xp = xp;
	}

	@Override
	public boolean give(Player player) {
		LevelManager.addXP(player, xp);
		return true;
	}
}
