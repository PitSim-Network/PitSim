package dev.kyro.pitsim.battlepass.rewards;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.battlepass.PassReward;
import dev.kyro.pitsim.commands.FreshCommand;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.MysticType;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.enums.PantColor;
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
			ItemStack jewelBow = FreshCommand.getFreshItem(MysticType.BOW, PantColor.JEWEL);
//			jewelBow = ItemManager.enableDropConfirm(jewelBow);
			NBTItem nbtItemBow = new NBTItem(jewelBow);
			nbtItemBow.setBoolean(NBTTag.IS_JEWEL.getRef(), true);
			EnchantManager.setItemLore(nbtItemBow.getItem(), pitPlayer.player);
			AUtil.giveItemSafely(pitPlayer.player, nbtItemBow.getItem());
		}
		return true;
	}

	@Override
	public ItemStack getDisplayItem(PitPlayer pitPlayer, boolean hasClaimed) {
		ItemStack itemStack = new AItemStackBuilder(Material.BOW, count)
				.setName("&b&lBow Reward")
				.setLore(new ALoreBuilder(
						"&7Reward: &b" + count + "x Hidden Jewel Bow"
				)).getItemStack();
		return itemStack;
	}
}
