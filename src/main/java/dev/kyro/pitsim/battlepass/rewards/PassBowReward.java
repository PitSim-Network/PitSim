package dev.kyro.pitsim.battlepass.rewards;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.aitems.MysticFactory;
import dev.kyro.pitsim.battlepass.PassReward;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.MysticType;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class PassBowReward extends PassReward {
	public int count;

	public PassBowReward(int count) {
		this.count = count;
	}

	@Override
	public boolean giveReward(PitPlayer pitPlayer) {
		if(Misc.getEmptyInventorySlots(pitPlayer.player) < count) {
			AOutput.error(pitPlayer.player, "&7Please make space in your inventory");
			return false;
		}

		for(int i = 0; i < count; i++) {
			ItemStack jewelBow = MysticFactory.getJewelItem(MysticType.BOW);
			AUtil.giveItemSafely(pitPlayer.player, jewelBow);
		}
		return true;
	}

	@Override
	public ItemStack getDisplayStack(PitPlayer pitPlayer, boolean hasClaimed) {
		ItemStack itemStack = new AItemStackBuilder(Material.BOW, count)
				.setName("&b&lBow Reward")
				.setLore(new ALoreBuilder(
						"&7Reward: &b" + count + "x Hidden Jewel Bow"
				)).getItemStack();
		return itemStack;
	}
}
