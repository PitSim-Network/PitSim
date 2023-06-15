package net.pitsim.pitsim.battlepass.rewards;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import net.pitsim.pitsim.aitems.MysticFactory;
import net.pitsim.pitsim.battlepass.PassReward;
import net.pitsim.pitsim.controllers.objects.PitPlayer;
import net.pitsim.pitsim.enums.MysticType;
import net.pitsim.pitsim.enums.PantColor;
import net.pitsim.pitsim.misc.Misc;
import org.bukkit.inventory.ItemStack;

public class PassPantsReward extends PassReward {
	public int count;

	public PassPantsReward(int count) {
		this.count = count;
	}

	@Override
	public boolean giveReward(PitPlayer pitPlayer) {
		if(Misc.getEmptyInventorySlots(pitPlayer.player) < count) {
			AOutput.error(pitPlayer.player, "&7Please make space in your inventory");
			return false;
		}

		for(int i = 0; i < count; i++) {
			ItemStack jewel = MysticFactory.getJewelItem(MysticType.PANTS);
			AUtil.giveItemSafely(pitPlayer.player, jewel);
		}
		return true;
	}

	@Override
	public ItemStack getDisplayStack(PitPlayer pitPlayer, boolean hasClaimed) {
		ItemStack itemStack = MysticFactory.getFreshItem(MysticType.PANTS, PantColor.JEWEL);
		new AItemStackBuilder(itemStack)
				.setName("&3&lPants Reward")
				.setLore(new ALoreBuilder(
						"&7Reward: &3" + count + "x Hidden Jewel Pants"
				));
		itemStack.setAmount(count);
		return itemStack;
	}
}
