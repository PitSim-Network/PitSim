package net.pitsim.pitsim.battlepass.rewards;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import net.pitsim.pitsim.aitems.MysticFactory;
import net.pitsim.pitsim.battlepass.PassReward;
import net.pitsim.pitsim.controllers.objects.PitPlayer;
import net.pitsim.pitsim.enums.MysticType;
import net.pitsim.pitsim.misc.Misc;
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
