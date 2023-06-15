package net.pitsim.pitsim.battlepass.rewards;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import net.pitsim.pitsim.aitems.misc.ChunkOfVile;
import net.pitsim.pitsim.battlepass.PassReward;
import net.pitsim.pitsim.controllers.ItemFactory;
import net.pitsim.pitsim.controllers.objects.PitPlayer;
import net.pitsim.pitsim.misc.Misc;
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
	public ItemStack getDisplayStack(PitPlayer pitPlayer, boolean hasClaimed) {
		ItemStack itemStack = new AItemStackBuilder(Material.COAL, count)
				.setName("&5&lVile Reward")
				.setLore(new ALoreBuilder(
						"&7Reward: &5" + count + "x Chunk of Vile"
				)).getItemStack();
		return itemStack;
	}
}
