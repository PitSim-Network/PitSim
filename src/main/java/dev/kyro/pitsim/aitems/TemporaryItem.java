package dev.kyro.pitsim.aitems;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.pitsim.enums.NBTTag;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public interface TemporaryItem {
	TemporaryType getTemporaryType();

	default ItemStack damage(ItemStack itemStack, int lives) {
		TemporaryType type = getTemporaryType();
		if(type == TemporaryType.LOST_ON_DEATH) {
			return new ItemStack(Material.AIR);
		} else if(type == TemporaryType.LOSES_LIVES_ON_DEATH) {
			NBTItem nbtItem = new NBTItem(itemStack);
			int newLives = nbtItem.getInteger(NBTTag.CURRENT_LIVES.getRef()) - lives;
			if(newLives <= 0) return new ItemStack(Material.AIR);
			nbtItem.setInteger(NBTTag.CURRENT_LIVES.getRef(), newLives);
			return nbtItem.getItem();
		}
		throw new RuntimeException();
	}

	default ItemStack takeLives(ItemStack itemStack, int lives) {
		return null;
	}

	default ItemStack breakItem(ItemStack itemStack) {
		return null;
	}

	enum TemporaryType {
		LOST_ON_DEATH,
		LOSES_LIVES_ON_DEATH
	}
}
