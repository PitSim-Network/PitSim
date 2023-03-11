package dev.kyro.pitsim.adarkzone.altar.pedestals;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.pitsim.adarkzone.altar.AltarPedestal;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class AltarPanel extends AGUIPanel {
	public AltarPanel(AGUI gui) {
		super(gui);

		int slot = 0;
		for(AltarPedestal pedestal : AltarPedestal.altarPedestals) {
			ItemStack item = pedestal.getItem(player);
			ALoreBuilder loreBuilder = new ALoreBuilder(item.getItemMeta().getLore());
			String status = "&eClick to activate!";
			if(!pedestal.isUnlocked(player)) status = "&cPedestal is locked!";
			else if(pedestal.isActivated(player)) status = "&aPedestal is activated!";
			loreBuilder.addLore("&7Status: " + status);

			ItemMeta meta = item.getItemMeta();
			meta.setLore(loreBuilder.getLore());
			item.setItemMeta(meta);

			getInventory().setItem(slot, item);

			slot += 2;
		}

		AItemStackBuilder confirm = new AItemStackBuilder(Material.EMERALD)
				.setName("&c&lClose");
	}

	@Override
	public String getName() {
		return "Tainted Altar";
	}

	@Override
	public int getRows() {
		return 3;
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		if(event.getClickedInventory().getHolder() != this) return;
		int slot = event.getSlot();

		for(int i = 0; i < AltarPedestal.altarPedestals.size(); i++) {
			if(slot != i * 2) continue;
			AltarPedestal pedestal = AltarPedestal.altarPedestals.get(i);

			if(!pedestal.isUnlocked(player)) {
				Sounds.ERROR.play(player);
				return;
			}

			if(pedestal.isActivated(player)) {
				pedestal.deactivate(player, false);
			} else {
				pedestal.activate(player);
			}
		}


	}

	@Override
	public void onOpen(InventoryOpenEvent event) {

	}

	@Override
	public void onClose(InventoryCloseEvent event) {

	}
}
