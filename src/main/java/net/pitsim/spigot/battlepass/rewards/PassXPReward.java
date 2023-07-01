package net.pitsim.spigot.battlepass.rewards;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import net.pitsim.spigot.battlepass.PassReward;
import net.pitsim.spigot.controllers.LevelManager;
import net.pitsim.spigot.controllers.PrestigeValues;
import net.pitsim.spigot.controllers.objects.PitPlayer;
import net.pitsim.spigot.misc.Formatter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class PassXPReward extends PassReward {
	public long xp;

	public PassXPReward(long xp) {
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
		return Math.pow(prestigeInfo.getXpMultiplier(), 2.0 / 3.0);
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
