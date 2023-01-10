package dev.kyro.pitsim.battlepass.rewards;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.aitems.misc.ChunkOfVile;
import dev.kyro.pitsim.battlepass.PassReward;
import dev.kyro.pitsim.controllers.ItemFactory;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class PassVileReward extends PassReward {
	public int count;

	public PassVileReward(int count) {
		this.count = count;
	}

	@Override
	public boolean giveReward(PitPlayer pitPlayer) {
		if(Misc.getEmptyInventorySlots(pitPlayer.player) < 1) {
			AOutput.error(pitPlayer.player, "&7Please make space in your inventory");
			return false;
		}

		ItemFactory.getItem(ChunkOfVile.class).giveItem(pitPlayer.player, count);
		return true;
	}

	@Override
	public ItemStack getDisplayItem(PitPlayer pitPlayer, boolean hasClaimed) {
		ItemStack itemStack = new AItemStackBuilder(Material.COAL, count)
				.setName("&5&lVile Reward")
				.setLore(new ALoreBuilder(
						"&7Reward: &5" + count + "x Chunk of Vile"
				)).getItemStack();
		return itemStack;
	}
}
