package dev.kyro.pitsim.battlepass.rewards;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.battlepass.PassReward;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class PassRenownReward extends PassReward {
	public int count;

	public PassRenownReward(int count) {
		this.count = count;
	}

	@Override
	public boolean giveReward(PitPlayer pitPlayer) {
		pitPlayer.renown += count;
		return true;
	}

	@Override
	public ItemStack getDisplayItem(PitPlayer pitPlayer, boolean hasClaimed) {
		ItemStack itemStack = new AItemStackBuilder(Material.BEACON)
				.setName("&6&lGold Reward")
				.setLore(new ALoreBuilder(
						"&7Reward: &e" + count + " renown"
				)).getItemStack();
		return itemStack;
	}
}
