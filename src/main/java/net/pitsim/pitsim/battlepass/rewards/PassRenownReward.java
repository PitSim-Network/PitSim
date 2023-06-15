package net.pitsim.pitsim.battlepass.rewards;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import net.pitsim.pitsim.battlepass.PassReward;
import net.pitsim.pitsim.controllers.objects.PitPlayer;
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
	public ItemStack getDisplayStack(PitPlayer pitPlayer, boolean hasClaimed) {
		ItemStack itemStack = new AItemStackBuilder(Material.BEACON)
				.setName("&e&lRenown Reward")
				.setLore(new ALoreBuilder(
						"&7Reward: &e" + count + " renown"
				)).getItemStack();
		return itemStack;
	}
}
