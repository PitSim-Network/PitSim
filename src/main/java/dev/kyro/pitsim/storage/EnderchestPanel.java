package dev.kyro.pitsim.storage;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.util.UUID;

public class EnderchestPanel extends AGUIPanel {

	public StorageProfile profile;

	public EnderchestPanel(AGUI gui, UUID storagePlayer) {
		super(gui);

		inventoryBuilder.createBorder(Material.STAINED_GLASS_PANE, 15);

		profile = StorageManager.getProfile(storagePlayer);
	}

	@Override
	public String getName() {
		return "Enderchest";
	}

	@Override
	public int getRows() {
		return 4;
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		if(event.getClickedInventory().getHolder() != this) return;
		int slot = event.getSlot();

		EnderchestGUI.EnderchestPages rank = EnderchestGUI.EnderchestPages.getRank(player);
		int accessiblePages = profile.getUUID().equals(player.getUniqueId()) ? rank.pages : StorageManager.MAX_ENDERCHEST_PAGES;

		if(slot == 8 && !player.getUniqueId().equals(profile.getUUID())) {
			EditSession session = StorageManager.getSession(player);
			session.playerClosed = false;
			player.openInventory(session.inventory.getInventory());
			session.playerClosed = true;
		}

		if(slot < 9 || slot >= StorageManager.MAX_ENDERCHEST_PAGES + 9) return;

		if((slot - 9) + 1 > accessiblePages) {
			event.setCancelled(true);
			AOutput.error(player, "&5&lRANK REQUIRED!&7 Browse ranks at &6&nhttps://store.pitsim.net");
			return;
		}

		if(!profile.isLoaded() || profile.isSaving()) return;

		EnderchestPage enderchestPage = profile.getEnderchestPage(slot - 9);

		if(StorageManager.isEditing(player)) {
			EditSession session = StorageManager.getSession(player);
			session.playerClosed = false;
			player.openInventory(enderchestPage.getInventory());
			session.playerClosed = true;
			return;
		}

		player.openInventory(enderchestPage.getInventory());
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
		EnderchestGUI.EnderchestPages rank = EnderchestGUI.EnderchestPages.getRank(player);

		for(int i = 9; i < 27; i++) {
			int pageIndex = (i - 9);

			EnderchestPage enderchestPage = profile.getEnderchestPage(pageIndex);
			AItemStackBuilder stackBuilder = new AItemStackBuilder(enderchestPage.getDisplayItem())
					.setName("&5&lENDERCHEST &7Page " + (enderchestPage.getIndex() + 1));
			ALoreBuilder lore = new ALoreBuilder();

			int accessiblePages = profile.getUUID().equals(player.getUniqueId()) ? rank.pages : StorageManager.MAX_ENDERCHEST_PAGES;

			if(pageIndex + 1 <= accessiblePages) {
				lore.addLore(
						"&7Status: &aUnlocked",
						"&7Items: &d" + enderchestPage.getItemCount() + "&7/&d" + StorageManager.ENDERCHEST_ITEM_SLOTS
				);
				Misc.addEnchantGlint(stackBuilder.getItemStack());
			} else {
				stackBuilder.getItemStack().setType(Material.BARRIER);
				lore.addLore(
						"&7Status: &cLocked",
						"&7Required Rank: " + EnderchestGUI.EnderchestPages.getMinimumRequiredRank(pageIndex + 1).rankName,
						"&7Store: &d&nstore.pitsim.net"
				);
			}

			stackBuilder.setLore(lore);
			getInventory().setItem(i, stackBuilder.getItemStack());

			if(player.getUniqueId().equals(profile.getUUID())) continue;

			AItemStackBuilder builder = new AItemStackBuilder(Material.CHEST);
			builder.setName("&6View Inventory");
			getInventory().setItem(8, builder.getItemStack());
		}
	}

	@Override
	public void onClose(InventoryCloseEvent event) {
		if(!StorageManager.isEditing((Player) event.getPlayer())) return;
		EditSession session = StorageManager.getSession(player);

		if(!session.playerClosed) return;
		session.end();
	}
}
