package dev.kyro.pitsim.battlepass.rewards;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.aitems.MysticFactory;
import dev.kyro.pitsim.battlepass.PassReward;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.MysticType;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.enums.PantColor;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class PassSwordReward extends PassReward {
	public int count;

	public PassSwordReward(int count) {
		this.count = count;
	}

	@Override
	public boolean giveReward(PitPlayer pitPlayer) {
		if(Misc.getEmptyInventorySlots(pitPlayer.player) < count) {
			AOutput.error(pitPlayer.player, "&7Please make space in your inventory");
			return false;
		}

		for(int i = 0; i < count; i++) {
			ItemStack jewelSword = MysticFactory.getFreshItem(MysticType.SWORD, PantColor.JEWEL);
//			jewelSword = ItemManager.enableDropConfirm(jewelSword);
			NBTItem nbtItemSword = new NBTItem(jewelSword);
			nbtItemSword.setBoolean(NBTTag.IS_JEWEL.getRef(), true);
			EnchantManager.setItemLore(nbtItemSword.getItem(), pitPlayer.player);
			AUtil.giveItemSafely(pitPlayer.player, nbtItemSword.getItem());
		}
		return true;
	}

	@Override
	public ItemStack getDisplayItem(PitPlayer pitPlayer, boolean hasClaimed) {
		ItemStack itemStack = new AItemStackBuilder(Material.GOLD_SWORD, count)
				.setName("&e&lSword Reward")
				.setLore(new ALoreBuilder(
						"&7Reward: &e" + count + "x Hidden Jewel Sword"
				)).getItemStack();
		return itemStack;
	}
}
