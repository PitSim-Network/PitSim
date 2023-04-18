package dev.kyro.pitsim.inventories;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.adarkzone.DarkzoneBalancing;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TaintedShopPanel extends AGUIPanel {
	public TaintedShopPanel(AGUI gui) {
		super(gui);
		inventoryBuilder.createBorder(Material.STAINED_GLASS_PANE, 7);

		getInventory().setItem(31, new AItemStackBuilder(Material.ARROW)
				.setName("&eBack")
				.setLore(new ALoreBuilder(
						"&7to home menu"
				))
				.getItemStack());

		placeItems();
	}

	public void placeItems() {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		int slot = 9;

		for(DarkzoneBalancing.ShopItem value : DarkzoneBalancing.ShopItem.values()) {
			while(slot % 9 == 0 || slot % 9 == 8) slot++;

			ItemStack itemStack = value.getItemStack();
			ItemMeta itemMeta = itemStack.getItemMeta();
			int cost = value.getSoulCost();
			ALoreBuilder lore = new ALoreBuilder(itemMeta.getLore());
			lore.addLore("",
					"&7Cost: &f" + cost + " Soul" + Misc.s(cost),
					"&7You have: &f" + pitPlayer.taintedSouls + " Soul" + Misc.s(pitPlayer.taintedSouls),
					"",
					pitPlayer.taintedSouls < cost ? "&cNot enough souls!" : "&eClick to purchase!"
			);
			itemMeta.setLore(lore.getLore());
			itemStack.setItemMeta(itemMeta);

			NBTItem nbtItem = new NBTItem(itemStack, true);
			nbtItem.setInteger(NBTTag.INVENTORY_INDEX.getRef(), value.ordinal());

			getInventory().setItem(slot, itemStack);

			slot++;
		}
	}

	@Override
	public String getName() {
		return "Tainted Shop";
	}

	@Override
	public int getRows() {
		return 4;
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		if(event.getClickedInventory().getHolder() != this) return;

		if(event.getSlot() == 31) {
			openPreviousGUI();
			return;
		}

		ItemStack itemStack = event.getCurrentItem();
		if(Misc.isAirOrNull(itemStack)) return;

		NBTItem nbtItem = new NBTItem(itemStack, true);
		if(!nbtItem.hasKey(NBTTag.INVENTORY_INDEX.getRef())) return;

		int index = nbtItem.getInteger(NBTTag.INVENTORY_INDEX.getRef());
		DarkzoneBalancing.ShopItem shopItem = DarkzoneBalancing.ShopItem.values()[index];

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		if(pitPlayer.taintedSouls < shopItem.getSoulCost()) {
			Sounds.NO.play(player);
			return;
		}

		Sounds.RENOWN_SHOP_PURCHASE.play(player);

		pitPlayer.taintedSouls -= shopItem.getSoulCost();
		AUtil.giveItemSafely(player, shopItem.getItemStack());

		placeItems();
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {

	}

	@Override
	public void onClose(InventoryCloseEvent event) {

	}
}
