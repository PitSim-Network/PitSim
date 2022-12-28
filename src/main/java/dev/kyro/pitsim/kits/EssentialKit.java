package dev.kyro.pitsim.kits;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.Kit;
import dev.kyro.pitsim.enums.KitItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class EssentialKit extends Kit {
	@Override
	public void addItems() {
		items.add(KitItem.DIAMOND_HELMET);
		items.add(KitItem.DIAMOND_CHESTPLATE);
		items.add(KitItem.DIAMOND_BOOTS);
	}

	@Override
	public ItemStack getDisplayItem() {
		ItemStack itemStack = new AItemStackBuilder(Material.DIAMOND_CHESTPLATE)
				.setName("&7Essential Kit")
				.setLore(new ALoreBuilder(
						"&7Contains Diamond Armor"
				)).getItemStack();
		return itemStack;
	}
}
