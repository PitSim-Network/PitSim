package dev.kyro.pitsim.aitems;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public interface TemporaryItem {
	TemporaryType getTemporaryType();

	default void onItemRemove(ItemStack itemStack) {}

	default int getLives(ItemStack itemStack) {
		TemporaryType type = getTemporaryType();
		if(type != TemporaryType.LOOSES_LIVES_ON_DEATH) throw new RuntimeException();

		NBTItem nbtItem = new NBTItem(itemStack);
		return nbtItem.getInteger(NBTTag.CURRENT_LIVES.getRef());
	}

	default int getMaxLives(ItemStack itemStack) {
		TemporaryType type = getTemporaryType();
		if(type != TemporaryType.LOOSES_LIVES_ON_DEATH) throw new RuntimeException();

		NBTItem nbtItem = new NBTItem(itemStack);
		return nbtItem.getInteger(NBTTag.MAX_LIVES.getRef());
	}

	default ItemStack addLives(ItemStack itemStack, int lives) {
		return setLives(itemStack, getLives(itemStack) + lives);
	}

//	This also increases lives
	default ItemStack addMaxLives(ItemStack itemStack, int lives) {
		int currentLives = getLives(itemStack);
		itemStack = setLives(itemStack, currentLives + lives);
		return setMaxLives(itemStack, currentLives + lives);
	}

	default ItemStack setLives(ItemStack itemStack, int lives) {
		PitItem pitItem = (PitItem) this;
		TemporaryType type = getTemporaryType();
		if(type != TemporaryType.LOOSES_LIVES_ON_DEATH) return itemStack;

		NBTItem nbtItem = new NBTItem(itemStack);
		nbtItem.setInteger(NBTTag.CURRENT_LIVES.getRef(), lives);

		itemStack = nbtItem.getItem();
		pitItem.updateItem(itemStack);
		return itemStack;
	}

	default ItemStack setMaxLives(ItemStack itemStack, int lives) {
		PitItem pitItem = (PitItem) this;
		TemporaryType type = getTemporaryType();
		if(type != TemporaryType.LOOSES_LIVES_ON_DEATH) return itemStack;

		NBTItem nbtItem = new NBTItem(itemStack);
		nbtItem.setInteger(NBTTag.MAX_LIVES.getRef(), lives);

		itemStack = nbtItem.getItem();
		pitItem.updateItem(itemStack);
		return itemStack;
	}

	default boolean isAtMaxLives(ItemStack itemStack) {
		TemporaryType type = getTemporaryType();
		if(type != TemporaryType.LOOSES_LIVES_ON_DEATH) throw new RuntimeException();

		return getLives(itemStack) == getMaxLives(itemStack);
	}

	default ItemDamageResult damage(ItemStack itemStack, int attemptToLoseLives) {
		PitItem pitItem = (PitItem) this;
		TemporaryType type = getTemporaryType();
		if(type == TemporaryType.LOST_ON_DEATH) {
			return new ItemDamageResult(new ItemStack(Material.AIR), 0);
		} else if(type == TemporaryType.LOOSES_LIVES_ON_DEATH) {
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
		LOOSES_LIVES_ON_DEATH
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
