package dev.kyro.pitsim.kits;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.Kit;
import dev.kyro.pitsim.enums.KitItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class GoldKit extends Kit {
	@Override
	public void addItems() {
		items.add(KitItem.EXE_MOCT_BOOST);
		items.add(KitItem.EXE_MOCT_SHARK);
		items.add(KitItem.MOCT_BOOST_BUMP);
		items.add(KitItem.MOCT_BOOST_ELEC);
		items.add(KitItem.STREAKING_BILL_LS);
		items.add(KitItem.STREAKING_CH_LS);
		items.add(KitItem.VOLLEY_FTTS);
	}

	@Override
	public ItemStack getDisplayStack() {
		ItemStack itemStack = new AItemStackBuilder(Material.GOLD_INGOT)
				.setName("&6Gold Streaking Kit")
				.setLore(new ALoreBuilder(
						"&7Contains &6Gold &7Streaking Mystics"
				)).getItemStack();
		return itemStack;
	}
}
