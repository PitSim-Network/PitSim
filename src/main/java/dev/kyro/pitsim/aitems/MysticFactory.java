package dev.kyro.pitsim.aitems;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.pitsim.aitems.mystics.*;
import dev.kyro.pitsim.controllers.ItemFactory;
import dev.kyro.pitsim.enums.MysticType;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.enums.PantColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MysticFactory {
	public static ItemStack getFreshItem(Player player, String type) {
		if(type.equals("sword")) {
			return getFreshItem(MysticType.SWORD, null);
		} else if(type.equals("bow")) {
			return getFreshItem(MysticType.BOW, null);
		} else if(type.equals("chestplate")) {
			if(!player.isOp()) return null;
			return getFreshItem(MysticType.TAINTED_CHESTPLATE, null);
		} else if(type.equals("scythe")) {
			if(!player.isOp()) return null;
			return getFreshItem(MysticType.TAINTED_SCYTHE, null);
		} else if(PantColor.getPantColor(type) != null) {
			return getFreshItem(MysticType.PANTS, PantColor.getPantColor(type));
		}

		return null;
	}

	public static ItemStack getFreshItem(MysticType type, PantColor pantColor) {
		if(type == MysticType.SWORD) {
			return ItemFactory.getItem(MysticSword.class).getItem();
		} else if(type == MysticType.BOW) {
			return ItemFactory.getItem(MysticBow.class).getItem();
		} else if(type == MysticType.TAINTED_SCYTHE) {
			return ItemFactory.getItem(TaintedScythe.class).getItem();
		} else if(type == MysticType.TAINTED_CHESTPLATE) {
			return ItemFactory.getItem(TaintedChestplate.class).getItem();
		} else {
			return ItemFactory.getItem(MysticPants.class).getItem(pantColor);
		}
	}

	public static boolean isFresh(ItemStack itemStack) {
		PitItem pitItem = ItemFactory.getItem(itemStack);
		if(pitItem == null || !pitItem.isMystic) return false;

		NBTItem nbtItem = new NBTItem(itemStack);
		Integer enchantNum = nbtItem.getInteger(NBTTag.ITEM_ENCHANTS.getRef());
		return enchantNum == 0;
	}
}
