package net.pitsim.pitsim.battlepass.rewards;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import net.pitsim.pitsim.aitems.misc.FunkyFeather;
import net.pitsim.pitsim.battlepass.PassReward;
import net.pitsim.pitsim.controllers.ItemFactory;
import net.pitsim.pitsim.controllers.objects.PitPlayer;
import net.pitsim.pitsim.misc.Misc;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class PassFeatherReward extends PassReward {
	public int count;

	public PassFeatherReward(int count) {
		this.count = count;
	}

	@Override
	public boolean giveReward(PitPlayer pitPlayer) {
		if(Misc.getEmptyInventorySlots(pitPlayer.player) < 1) {
			AOutput.error(pitPlayer.player, "&7Please make space in your inventory");
			return false;
		}

		ItemFactory.getItem(FunkyFeather.class).giveItem(pitPlayer.player, count);
		return true;
	}

	@Override
	public ItemStack getDisplayStack(PitPlayer pitPlayer, boolean hasClaimed) {
		ItemStack itemStack = new AItemStackBuilder(Material.FEATHER, count)
				.setName("&3&lFeather Reward")
				.setLore(new ALoreBuilder(
						"&7Reward: &3" + count + "x Funky Feather"
				)).getItemStack();
		return itemStack;
	}
}
