package dev.kyro.pitsim.adarkzone.altar.pedestals;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.pitsim.adarkzone.altar.AltarManager;
import dev.kyro.pitsim.adarkzone.altar.AltarPedestal;
import dev.kyro.pitsim.adarkzone.altar.AltarRewards;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class AltarPanel extends AGUIPanel {

	public PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
	public int totalCost = AltarPedestal.getTotalCost(player);

	public AltarPanel(AGUI gui) {
		super(gui);

		setItems();
	}

	public void setItems() {

		int slot = 0;
		for(AltarPedestal pedestal : AltarPedestal.altarPedestals) {
			ItemStack item = pedestal.getItem(player);
			ALoreBuilder loreBuilder = new ALoreBuilder(item.getItemMeta().getLore());
			String status = "&eClick to activate!";
			if(!pedestal.isUnlocked(player)) status = "&cPedestal is locked!";
			else if(pedestal.isActivated(player)) status = "&eClick to deactivate!";
			loreBuilder.addLore("", status);

			ItemMeta meta = item.getItemMeta();
			meta.setLore(loreBuilder.getLore());
			item.setItemMeta(meta);

			getInventory().setItem(slot, item);

			slot += 2;
		}

		String costStatus = totalCost > pitPlayer.taintedSouls ? "&cYou do not have enough souls!" : "&aClick to confirm your selections!";
		double multiplier = AltarRewards.getSoulMultiplier(player) * (AltarPedestal.getPedestal(WealthPedestal.class).isActivated(player) ? AltarPedestal.WEALTH_MULTIPLIER : 1);
		String multiplierStatus = AltarPedestal.getPedestal(TurmoilPedestal.class).isActivated(player) ? "???" : multiplier + "x";

		ALoreBuilder loreBuilder = new ALoreBuilder("");
		for(AltarPedestal altarPedestal : AltarPedestal.altarPedestals) {
			loreBuilder.addLore(altarPedestal.getDisplayName() + "&7: " + (altarPedestal.isActivated(player) ?
					"&a&lACTIVE" : "&c&lINACTIVE"));
		}
		loreBuilder.addLore(
				"",
				"&7Total Cost: &f" + totalCost + " Souls",
				"&7Your Souls: &f" + pitPlayer.taintedSouls + " Souls",
				"",
				costStatus
		);


		AItemStackBuilder confirm = new AItemStackBuilder(Material.STAINED_CLAY, 1, pitPlayer.taintedSouls < totalCost ? 14 : 5)
				.setName((pitPlayer.taintedSouls < totalCost ? "&c" : "&a") + "Confirm Selections")
				.setLore(loreBuilder);

		getInventory().setItem(22, confirm.getItemStack());
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

			totalCost = AltarPedestal.getTotalCost(player);
			setItems();
		}

		if(slot == 22) {
			if(pitPlayer.taintedSouls < totalCost) {
				Sounds.ERROR.play(player);
				return;
			}

			AltarManager.activateAltar(player);
			player.closeInventory();
		}


	}

	@Override
	public void onOpen(InventoryOpenEvent event) {

	}

	@Override
	public void onClose(InventoryCloseEvent event) {

	}
}
