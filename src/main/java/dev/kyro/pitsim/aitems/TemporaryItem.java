package dev.kyro.pitsim.aitems;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.pitsim.enums.NBTTag;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public interface TemporaryItem {
	TemporaryType getTemporaryType();

	default ItemDamageResult damage(PitItem pitItem, ItemStack itemStack, int lives) {
		TemporaryType type = getTemporaryType();
		if(type == TemporaryType.LOST_ON_DEATH) {
			return new ItemDamageResult(new ItemStack(Material.AIR), true);
		} else if(type == TemporaryType.LOSES_LIVES_ON_DEATH) {
			NBTItem nbtItem = new NBTItem(itemStack);
			int newLives = nbtItem.getInteger(NBTTag.CURRENT_LIVES.getRef()) - lives;
			if(newLives <= 0) return new ItemDamageResult(new ItemStack(Material.AIR), true);
			nbtItem.setInteger(NBTTag.CURRENT_LIVES.getRef(), newLives);
			pitItem.updateItem(nbtItem.getItem());
			return new ItemDamageResult(nbtItem.getItem(), lives);
		}
		throw new RuntimeException();
	}

	enum TemporaryType {
		LOST_ON_DEATH,
		LOSES_LIVES_ON_DEATH
	}

	public class ItemDamageResult {
		private final ItemStack itemStack;
		private int livesLost = 0;
		private boolean brokeItem;

		public ItemDamageResult(ItemStack itemStack, int livesLost) {
			this.itemStack = itemStack;
			this.livesLost = livesLost;
		}

		public ItemDamageResult(ItemStack itemStack, boolean brokeItem) {
			this.itemStack = itemStack;
			this.brokeItem = brokeItem;
		}

		public ItemStack getItemStack() {
			return itemStack;
		}

		public int getLivesLost() {
			return livesLost;
		}

		public boolean isItemBroken() {
			return brokeItem;
		}
	}
}
