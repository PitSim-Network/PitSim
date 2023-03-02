package dev.kyro.pitsim.aitems;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public interface TemporaryItem {
	TemporaryType getTemporaryType();

	default void onItemRemove(ItemStack itemStack) {};

	default ItemDamageResult damage(PitItem pitItem, ItemStack itemStack, int attemptToLoseLives) {
		TemporaryType type = getTemporaryType();
		if(type == TemporaryType.LOST_ON_DEATH) {
			return new ItemDamageResult(new ItemStack(Material.AIR), 0);
		} else if(type == TemporaryType.LOSES_LIVES_ON_DEATH) {
			NBTItem nbtItem = new NBTItem(itemStack);
			int currentLives = nbtItem.getInteger(NBTTag.CURRENT_LIVES.getRef());
			int livesLost = Math.min(attemptToLoseLives, currentLives);
			nbtItem.setInteger(NBTTag.CURRENT_LIVES.getRef(), currentLives - livesLost);
			pitItem.updateItem(nbtItem.getItem());
			return new ItemDamageResult(nbtItem.getItem(), livesLost);
		}
		throw new RuntimeException();
	}

	enum TemporaryType {
		LOST_ON_DEATH,
		LOSES_LIVES_ON_DEATH
	}

	class ItemDamageResult {
		private final ItemStack itemStack;
		private final int livesLost;

		public ItemDamageResult(ItemStack itemStack, int livesLost) {
			this.itemStack = itemStack;
			this.livesLost = livesLost;
		}

		public ItemStack getItemStack() {
			return itemStack;
		}

		public int getLivesLost() {
			return livesLost;
		}

		public boolean wasRemoved() {
			return Misc.isAirOrNull(itemStack);
		}
	}
}
