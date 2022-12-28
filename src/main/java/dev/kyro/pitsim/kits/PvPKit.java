package dev.kyro.pitsim.kits;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.Kit;
import dev.kyro.pitsim.enums.KitItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class PvPKit extends Kit {
	@Override
	public void addItems() {
		items.add(KitItem.BILL_STOMP_LS);
		items.add(KitItem.BILL_LS_PF);
		items.add(KitItem.CH_LS);
		items.add(KitItem.PERUN_GAMBLE_STOMP);
		items.add(KitItem.PERUN_CHEAL_CD);
		items.add(KitItem.RGM_MIRROR_PROT);
		items.add(KitItem.RGM_CF_PROT);
		items.add(KitItem.REG_MIRROR_PROT);
		items.add(KitItem.REG_SOLI_LASTSTAND);
		items.add(KitItem.MLB_DRAIN);
		items.add(KitItem.MLB_PIN);
		items.add(KitItem.MLB_WASP);
		items.add(KitItem.MLB_TELE);
	}

	@Override
	public ItemStack getDisplayItem() {
		ItemStack itemStack = new AItemStackBuilder(Material.GOLD_SWORD)
				.setName("&cPvP Kit")
				.setLore(new ALoreBuilder(
						"&7Contains &cCombat &7Mystics"
				)).getItemStack();
		return itemStack;
	}
}
