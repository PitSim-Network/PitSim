package dev.kyro.pitsim.battlepass.rewards;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.battlepass.PassReward;
import dev.kyro.pitsim.controllers.LevelManager;
import dev.kyro.pitsim.controllers.PrestigeValues;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.misc.Formatter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class PassXpReward extends PassReward {
	public long xp;

	public PassXpReward(long xp) {
		this.xp = xp;
	}

	@Override
	public boolean giveReward(PitPlayer pitPlayer) {
		LevelManager.addXP(pitPlayer.player, (long) (xp * getMultiplier(pitPlayer)));
		return true;
	}

	@Override
	public double getMultiplier(PitPlayer pitPlayer) {
		PrestigeValues.PrestigeInfo prestigeInfo = PrestigeValues.getPrestigeInfo(pitPlayer.prestige);
		return Math.sqrt(prestigeInfo.getXpMultiplier());
	}

	@Override
	public ItemStack getDisplayStack(PitPlayer pitPlayer, boolean hasClaimed) {
		ItemStack itemStack = new AItemStackBuilder(Material.INK_SACK, 1, 12)
				.setName("&b&lXP Reward")
				.setLore(new ALoreBuilder(
						"&7Reward: &b+" + Formatter.formatLarge((long) (xp * getMultiplier(pitPlayer))) + " XP"
				)).getItemStack();
		return itemStack;
	}
}
