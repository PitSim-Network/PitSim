package dev.kyro.pitsim.battlepass.rewards;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.battlepass.PassReward;
import dev.kyro.pitsim.brewing.objects.BrewingIngredient;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.misc.Misc;
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
	public ItemStack getDisplayItem(PitPlayer pitPlayer, boolean hasClaimed) {
		ItemStack itemStack = ingredient.getItem();
		itemStack.setAmount(count);
		return itemStack;
	}
}
