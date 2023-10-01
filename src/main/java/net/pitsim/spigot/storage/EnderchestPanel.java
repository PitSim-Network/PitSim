package net.pitsim.spigot.storage;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
import net.pitsim.spigot.enums.RankInformation;
import net.pitsim.spigot.inventories.view.ViewGUI;
import net.pitsim.spigot.misc.Misc;
import net.pitsim.spigot.misc.Sounds;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class EnderchestPanel extends AGUIPanel {
	public AGUI enderchestGUI;
	public StorageProfile profile;

	public EnderchestPanel(AGUI gui, StorageProfile profile) {
		super(gui);
		this.enderchestGUI = gui;
		this.profile = profile;
		inventoryBuilder.createBorder(Material.STAINED_GLASS_PANE, 15);

		if(!isAdminSession() && !isViewSession()) addTaggedItem(35, () -> new AItemStackBuilder(Material.ARMOR_STAND)
				.setName("&2Wardrobe")
				.setLore(new ALoreBuilder(
						"&7Click to open your wardrobe"
				))
				.getItemStack(), event -> openPanel(((EnderchestGUI) enderchestGUI).wardrobePanel)).setItem();
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

		RankInformation rank = RankInformation.getRank(profile.getUniqueID());
		int accessiblePages = isAdminSession() ? StorageManager.MAX_ENDERCHEST_PAGES : rank.enderchestPages;

		if(slot == 8 && isAdminSession()) {
			EditSession session = StorageManager.getSession(player);
			session.playerClosed = false;
			player.openInventory(session.inventory.getInventory());
			session.playerClosed = true;
		} else if(slot == 8 && isViewSession()) {
			ViewGUI viewGUI = ViewGUI.viewGUIs.get(player.getUniqueId());
			viewGUI.playerClosed = false;
			openPanel(viewGUI.mainViewPanel);
		}

		if(slot < 9 || slot >= StorageManager.MAX_ENDERCHEST_PAGES + 9) return;

		if((slot - 9) + 1 > accessiblePages) {
			event.setCancelled(true);
			if(!isViewSession() && !isAdminSession()) AOutput.error(player, "&5&lRANK REQUIRED!&7 Browse ranks at &6&nhttps://store.pitsim.net");
			Sounds.ERROR.play(player);
			return;
		}

		if(!profile.isLoaded()) return;

		EnderchestPage enderchestPage = profile.getEnderchestPage(slot - 9);

		if(event.getClick() == ClickType.LEFT || event.getClick() == ClickType.SHIFT_LEFT) {
			if(StorageManager.isEditing(player)) {
				EditSession session = StorageManager.getSession(player);
				session.playerClosed = false;
				player.openInventory(enderchestPage.getInventory());
				session.playerClosed = true;
				return;
			}

			if(getViewGUI() != null) {
				ViewGUI viewGUI = getViewGUI();
				viewGUI.playerClosed = false;
			}
			player.openInventory(enderchestPage.getInventory());

		} else if(event.getClick() == ClickType.RIGHT || event.getClick() == ClickType.RIGHT) {
			if(isViewSession() || isAdminSession()) return;
			if(enderchestPage.isWardrobeEnabled()) {
				AOutput.send(player, "&2&lWARDROBE!&7 Wardrobe &cdisabled &7for " + enderchestPage.getDisplayName());
			} else {
				AOutput.send(player, "&2&lWARDROBE!&7 Wardrobe &aenabled &7for " + enderchestPage.getDisplayName());
			}
			Sounds.SUCCESS.play(player);
			enderchestPage.setWardrobeEnabled(!enderchestPage.isWardrobeEnabled());
			setInventory();
		}
	}

	@Override
	public void setInventory() {
		super.setInventory();
		RankInformation rank = RankInformation.getRank(profile.getUniqueID());
		int accessiblePages = isAdminSession() ? StorageManager.MAX_ENDERCHEST_PAGES : rank.enderchestPages;

		for(int i = 9; i < 27; i++) {
			int pageIndex = (i - 9);

			EnderchestPage enderchestPage = profile.getEnderchestPage(pageIndex);
			AItemStackBuilder stackBuilder = new AItemStackBuilder(enderchestPage.getDisplayItem())
					.setName(enderchestPage.getDisplayName());
			ALoreBuilder lore = new ALoreBuilder();

			if(pageIndex + 1 <= accessiblePages) {
				lore.addLore(
						"&7Status: &aUnlocked",
						"&7Wardrobe: " + (enderchestPage.isWardrobeEnabled() ? "&aEnabled" : "&cDisabled"),
						"&7Items: &d" + enderchestPage.getItemCount() + "&7/&d" + StorageManager.ENDERCHEST_ITEM_SLOTS,
						"",
						"&eLeft-Click to open!"
				);
				if(!isAdminSession() && !isViewSession()) lore.addLore("&eRight-Click to toggle wardrobe!");
				Misc.addEnchantGlint(stackBuilder.getItemStack());
			} else {
				stackBuilder.getItemStack().setType(Material.BARRIER);
				lore.addLore(
						"&7Status: &cLocked",
						"&7Required Rank: " + RankInformation.getMinimumRankForPages(pageIndex + 1).rankName,
						"&7Store: &d&nstore.pitsim.net"
				);
			}

			stackBuilder.setLore(lore);
			getInventory().setItem(i, stackBuilder.getItemStack());

			if(!isAdminSession() && !isViewSession()) continue;

			AItemStackBuilder builder = new AItemStackBuilder(Material.CHEST);
			builder.setName("&6View Inventory");
			getInventory().setItem(8, builder.getItemStack());
		}
		updateInventory();
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
		setInventory();

		if(getViewGUI() != null) {
			ViewGUI viewGUI = getViewGUI();
			viewGUI.playerClosed = true;
		}
	}

	@Override
	public void onClose(InventoryCloseEvent event) {

		if(StorageManager.isEditing((Player) event.getPlayer())) {
			EditSession session = StorageManager.getSession(player);

			if(!session.playerClosed) return;
			session.end();
		}
	}

	public boolean isAdminSession() {
		boolean edit = false;
		for(EditSession editSession : StorageManager.editSessions) {
			if(editSession.getStaffMember().getUniqueId().equals(player.getUniqueId())) {
				edit = true;
				break;
			}
		}

		return !profile.getUniqueID().equals(player.getUniqueId()) && edit;
	}

	public ViewGUI getViewGUI() {
		return ViewGUI.viewGUIs.get(player.getUniqueId());
	}

	public boolean isViewSession() {
		return StorageManager.viewProfiles.contains(profile);
	}
}
