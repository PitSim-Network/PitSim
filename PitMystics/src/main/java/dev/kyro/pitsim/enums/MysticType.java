package dev.kyro.pitsim.enums;

import org.bukkit.inventory.ItemStack;

public enum MysticType {

	SWORD,
	BOW,
	PANTS;

	public static MysticType getMysticType(ItemStack itemStack) {

		if(itemStack == null) return null;

		switch(itemStack.getType()) {

			case GOLD_SWORD:
				return SWORD;
			case BOW:
				return BOW;
			case LEATHER_LEGGINGS:
				return PANTS;
		}
		return null;
	}
}
