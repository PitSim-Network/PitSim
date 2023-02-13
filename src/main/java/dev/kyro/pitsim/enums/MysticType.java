package dev.kyro.pitsim.enums;

import dev.kyro.pitsim.aitems.PitItem;
import dev.kyro.pitsim.controllers.ItemFactory;
import org.bukkit.inventory.ItemStack;

public enum MysticType {

	SWORD("Sword"),
	BOW("Bow"),
	PANTS("Pants"),

	TAINTED_SCYTHE("Tainted Scythe"),
	TAINTED_CHESTPLATE("Tainted Chestplate");

	public String displayName;

	MysticType(String displayName) {
		this.displayName = displayName;
	}

	public static MysticType getMysticType(ItemStack itemStack) {
		PitItem pitItem = ItemFactory.getItem(itemStack);
		if(pitItem == null || !pitItem.isMystic) return null;

		switch(itemStack.getType()) {

			case GOLD_SWORD:
			case STONE_SWORD:
				return SWORD;
			case BOW:
				return BOW;
			case LEATHER_LEGGINGS:
			case CHAINMAIL_LEGGINGS:
				return PANTS;
			case GOLD_HOE:
			case STONE_HOE:
				return TAINTED_SCYTHE;
			case LEATHER_CHESTPLATE:
			case CHAINMAIL_CHESTPLATE:
				return TAINTED_CHESTPLATE;
		}
		return null;
	}

	public boolean isTainted() {
		return this == TAINTED_CHESTPLATE || this == TAINTED_SCYTHE;
	}
}
