package dev.kyro.pitsim.battlepass.rewards;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.battlepass.PassReward;
import dev.kyro.pitsim.controllers.LevelManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class PassXpReward extends PassReward {
	public long xp;

	public PassXpReward(long xp) {
		this.xp = xp;
	}

	@Override
	public boolean giveReward(PitPlayer pitPlayer) {
		LevelManager.addXP(pitPlayer.player, xp);
		Sounds.LEVEL_UP.play(pitPlayer.player);
		return true;
	}

	@Override
	public ItemStack getDisplayItem(boolean hasClaimed) {
		ItemStack itemStack = new AItemStackBuilder(Material.INK_SACK, 1, 12)
				.setName("&b&lXP Reward")
				.setLore(new ALoreBuilder(
						"&b+" + Misc.formatLarge(xp) + " XP"
				)).getItemStack();
		return itemStack;
	}
}
