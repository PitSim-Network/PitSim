package dev.kyro.pitsim.storage;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.tutorial.HelpItemStacks;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class EditInventoryPanel implements InventoryHolder, Listener {

	private Inventory inventory;
	public EditSession session;

	public boolean playerEdit = true;
	public BukkitTask runnable;

	public EditInventoryPanel(EditSession session) {
		this.session = session;
		buildInventory();

		runnable = new BukkitRunnable() {
			@Override
			public void run() {

				if(!playerEdit || session.getEditType() != EditType.ONLINE) {
					for(int i = 1; i < 5; i++) {
						session.getStorageProfile().getArmor()[i - 1] = inventory.getItem(i);
					}

					for(int i = 9; i < inventory.getSize(); i++) {
						session.getStorageProfile().getInventory()[i - 9] = inventory.getItem(i);
					}

					if(session.getEditType() == EditType.ONLINE) {
						Player onlinePlayer = Bukkit.getPlayer(session.getPlayerUUID());
						onlinePlayer.getInventory().setContents(session.getStorageProfile().getInventory());
						onlinePlayer.getInventory().setArmorContents(session.getStorageProfile().getArmor());
						onlinePlayer.updateInventory();
					}
				} else {
					Player player = Bukkit.getPlayer(session.getPlayerUUID());
					for(int i = 1; i < 5; i++) {
						inventory.setItem(i, player.getInventory().getArmorContents()[i - 1]);
					}

					for(int i = 9; i < inventory.getSize(); i++) {
						inventory.setItem(i, player.getInventory().getContents()[i - 9]);
					}
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0, 1);
	}

	@Override
	public Inventory getInventory() {
		return inventory;
	}

	public void buildInventory() {
		inventory = Bukkit.createInventory(this, 9 * 5, "Player Inventory");

		for(int i = 1; i < 5; i++) {
			inventory.setItem(i, session.getStorageProfile().getArmor()[i - 1]);
		}

		for(int i = 9; i < inventory.getSize(); i++) {
			inventory.setItem(i, session.getStorageProfile().getInventory()[i - 9]);
		}

		AItemStackBuilder builder = new AItemStackBuilder(Material.ENDER_CHEST);
		builder.setName("&5View Enderchest");
		inventory.setItem(8, builder.getItemStack());

		getInventory().setItem(0, HelpItemStacks.getAdminEditItemStack());

		for(int i = 5; i < 8; i++) {
			ItemStack pane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
			ItemMeta paneMeta = pane.getItemMeta();
			paneMeta.setDisplayName("");
			pane.setItemMeta(paneMeta);
			getInventory().setItem(i, pane);
		}
	}

	@EventHandler
	public void onClose(InventoryCloseEvent event) {
		if(session == null || session.inventory == null) return;
		Inventory inventory = event.getInventory();

		if(inventory.getHolder() != this) return;
		session.inventory = this;

		if(!session.playerClosed) {
			session.playerClosed = true;
			return;
		}

		HandlerList.unregisterAll(this);
		if(runnable != null) runnable.cancel();

		session.end();
	}

	@EventHandler
	public void onEditSessionClick(InventoryClickEvent event) {
		if(session == null || session.inventory == null || event.getClickedInventory() == null) return;
		Inventory inventory = event.getClickedInventory();

		if(inventory.equals(session.getStaffMember().getInventory()) && event.isShiftClick() && inventory.equals(event.getClickedInventory())) {
			event.setCancelled(true);
			return;
		}

		if(inventory.getHolder() != this) return;
		if(event.isShiftClick()) {
			event.setCancelled(true);
			return;
		}

		if(event.getWhoClicked().getUniqueId().equals(session.getPlayerUUID()) && !playerEdit) {
			event.setCancelled(true);
			return;
		}

		if(event.getSlot() == 8) {
			EnderchestGUI gui = new EnderchestGUI(session.getStaffMember(), session.getPlayerUUID());
			session.playerClosed = false;
			gui.open();
		}

		if(event.getSlot() == 0 || event.getSlot() > 4 && event.getSlot() < 9) event.setCancelled(true);

		if(session.getEditType() != EditType.ONLINE) return;

		if(event.getWhoClicked() == session.getStaffMember()) playerEdit = false;
		new BukkitRunnable() {
			@Override
			public void run() {
				playerEdit = true;
			}
		}.runTaskLater(PitSim.INSTANCE, 2);
	}

	@EventHandler
	public void onEditSessionClick(InventoryDragEvent event) {
		if(session == null || session.inventory == null) return;
		Inventory inventory = event.getInventory();

		if(inventory.getHolder() != this) return;

		if(event.getWhoClicked().getUniqueId().equals(session.getPlayerUUID()) && !playerEdit) {
			event.setCancelled(true);
			return;
		}

		if(session.getEditType() != EditType.ONLINE) return;

		if(event.getWhoClicked() == session.getStaffMember()) playerEdit = false;
		new BukkitRunnable() {
			@Override
			public void run() {
				playerEdit = true;
			}
		}.runTaskLater(PitSim.INSTANCE, 2);
	}

}
