package net.pitsim.pitsim.battlepass.rewards;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import net.pitsim.pitsim.battlepass.PassReward;
import net.pitsim.pitsim.brewing.objects.BrewingIngredient;
import net.pitsim.pitsim.controllers.objects.PitPlayer;
import net.pitsim.pitsim.misc.Misc;
import org.bukkit.inventory.ItemStack;

public class PassDarkzoneDropReward extends PassReward {
	public BrewingIngredient ingredient;
	public int count;

	public PassDarkzoneDropReward(int tier, int count) {
		this.ingredient = BrewingIngredient.getIngredientFromTier(tier);
		this.count = count;
	}

	@Override
	public boolean giveReward(PitPlayer pitPlayer) {
		if(Misc.getEmptyInventorySlots(pitPlayer.player) < 1) {
			AOutput.error(pitPlayer.player, "&7Please make space in your inventory");
			return false;
		}

		ItemStack itemStack = ingredient.getItem();
		itemStack.setAmount(count);
		AUtil.giveItemSafely(pitPlayer.player, itemStack);
		return true;
	}

	@Override
	public ItemStack getDisplayStack(PitPlayer pitPlayer, boolean hasClaimed) {
		ItemStack itemStack = ingredient.getItem();
		itemStack.setAmount(count);
		return itemStack;
	}
}
