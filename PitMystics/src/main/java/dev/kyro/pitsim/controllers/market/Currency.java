package dev.kyro.pitsim.controllers.market;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum Currency {

	PURE("&ePure", "pure", new AItemStackBuilder(Material.STORAGE_MINECART, 1).setName("&ePure").getItemStack()),
	PHILO("&aPhilo", "philo", new AItemStackBuilder(Material.CACTUS, 1).setName("&aPhilo").getItemStack()),
	FEATHER("&fFeathers", "feathers", new AItemStackBuilder(Material.FEATHER, 1).setName("&fFeathers").getItemStack()),
	WATER("&9Water", "water", new AItemStackBuilder(Material.WATER_BUCKET, 1).setName("&9Water").getItemStack());

	public String displayName;
	public String refName;
	public ItemStack displayItem;

	Currency(String displayName, String refName, ItemStack displayItem) {
		this.displayName = displayName;
		this.refName = refName;
		this.displayItem = displayItem;
	}

	public static Currency getCurrency(String refName) {

		for(Currency value : values()) {
			if(value.refName.equalsIgnoreCase(refName)) return value;
		}
		return null;
	}
}
