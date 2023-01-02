package dev.kyro.pitsim.adarkzone;

import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class DropPool {
	public Map<ItemStack, Double> dropPool = new HashMap<>();

	public DropPool() {
	}

	public DropPool addItem(ItemStack item, double chance) {
		dropPool.put(item, chance);
		return this;
	}
}
