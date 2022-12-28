package dev.kyro.pitsim.kits;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.Kit;
import dev.kyro.pitsim.enums.KitItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class XPKit extends Kit {
	@Override
	public void addItems() {
		items.add(KitItem.EXE_SWEATY);
		items.add(KitItem.SWEATY_GHEART);
		items.add(KitItem.SWEATY_ELEC);
		items.add(KitItem.STREAKING_BILL_LS);
		items.add(KitItem.STREAKING_CH_LS);
		items.add(KitItem.VOLLEY_FTTS);
	}

	@Override
	public ItemStack getDisplayItem() {
		ItemStack itemStack = new AItemStackBuilder(Material.WHEAT)
				.setName("&bXP Streaking Kit")
				.setLore(new ALoreBuilder(
						"&7Contains &bXP &7Streaking Mystics"
				)).getItemStack();
		return itemStack;
	}
}
