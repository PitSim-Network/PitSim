package net.pitsim.pitsim.kits;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import net.pitsim.pitsim.controllers.objects.Kit;
import net.pitsim.pitsim.enums.KitItem;
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
	public ItemStack getDisplayStack() {
		ItemStack itemStack = new AItemStackBuilder(Material.DIAMOND_CHESTPLATE)
				.setName("&7Essential Kit")
				.setLore(new ALoreBuilder(
						"&7Contains Diamond Armor"
				)).getItemStack();
		return itemStack;
	}
}
