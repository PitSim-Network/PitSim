package dev.kyro.pitsim.battlepass.rewards;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.aitems.MysticFactory;
import dev.kyro.pitsim.battlepass.PassReward;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.MysticType;
import dev.kyro.pitsim.enums.PantColor;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class PassScytheReward extends PassReward {
	public int count;

	public PassScytheReward(int count) {
		this.count = count;
	}

	@Override
	public boolean giveReward(PitPlayer pitPlayer) {
		if(Misc.getEmptyInventorySlots(pitPlayer.player) < count) {
			AOutput.error(pitPlayer.player, "&7Please make space in your inventory");
			return false;
		}

		for(int i = 0; i < count; i++) {
			ItemStack scythe = MysticFactory.getFreshItem(MysticType.TAINTED_SCYTHE, PantColor.TAINTED);
			AUtil.giveItemSafely(pitPlayer.player, scythe);
		}
		return true;
	}

	@Override
	public ItemStack getDisplayItem(PitPlayer pitPlayer, boolean hasClaimed) {
		ItemStack itemStack = new AItemStackBuilder(Material.GOLD_HOE, count)
				.setName("&5Scythe Reward")
				.setLore(new ALoreBuilder(
						"&7Reward: &5" + count + "x Fresh Tainted Scythe"
				)).getItemStack();
		return itemStack;
	}
}
