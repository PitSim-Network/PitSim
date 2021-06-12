package dev.kyro.pitsim.enums;

import org.bukkit.inventory.ItemStack;

public enum MysticType {

	SWORD("Sword"),
	BOW("Bow"),
	PANTS("Pants");

	public String displayName;

	MysticType(String displayName) {
		this.displayName = displayName;
	}

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
