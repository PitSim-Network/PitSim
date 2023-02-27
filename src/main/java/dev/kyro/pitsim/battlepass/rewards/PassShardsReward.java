package dev.kyro.pitsim.battlepass.rewards;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.aitems.misc.AncientGemShard;
import dev.kyro.pitsim.battlepass.PassReward;
import dev.kyro.pitsim.controllers.ItemFactory;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class PassShardsReward extends PassReward {
	public int count;

	public PassShardsReward(int count) {
		this.count = count;
	}

	@Override
	public boolean giveReward(PitPlayer pitPlayer) {
		if(Misc.getEmptyInventorySlots(pitPlayer.player) < 1) {
			AOutput.error(pitPlayer.player, "&7Please make space in your inventory");
			return false;
		}

		ItemFactory.getItem(AncientGemShard.class).giveItem(pitPlayer.player, count);
		return true;
	}

	@Override
	public ItemStack getDisplayItem(PitPlayer pitPlayer, boolean hasClaimed) {
		ItemStack itemStack = new AItemStackBuilder(Material.PRISMARINE_SHARD, count)
				.setName("&a&lShard Reward")
				.setLore(new ALoreBuilder(
						"&7Reward: &a" + count + "x Ancient Gem Shard"
				)).getItemStack();
		return itemStack;
	}
}
