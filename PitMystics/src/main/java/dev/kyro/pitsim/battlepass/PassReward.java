package dev.kyro.pitsim.battlepass;

import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.inventory.ItemStack;

public abstract class PassReward {

	public abstract boolean giveReward(PitPlayer pitPlayer);
	public abstract ItemStack getDisplayItem(boolean hasClaimed);
}
