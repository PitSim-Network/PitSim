package dev.kyro.pitsim.storage;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.pitsim.PitSim;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
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

		if(session.getEditType() != EditType.ONLINE) return;
		runnable = new BukkitRunnable() {
			@Override
			public void run() {
				Player player = Bukkit.getPlayer(session.getPlayerUUID());
				if(!playerEdit) {
					for(int i = 0; i < 4; i++) {
						session.getStorageProfile().armor[i] = inventory.getItem(i);
					}

					for(int i = 9; i < inventory.getSize(); i++) {
						session.getStorageProfile().cachedInventory[i - 9] = inventory.getItem(i);
					}

					if(session.getEditType() == EditType.ONLINE) {
						Player onlinePlayer = Bukkit.getPlayer(session.getPlayerUUID());
						onlinePlayer.getInventory().setContents(session.getStorageProfile().cachedInventory);
						onlinePlayer.getInventory().setArmorContents(session.getStorageProfile().armor);
						onlinePlayer.updateInventory();
					}
				} else {
					for(int i = 0; i < 4; i++) {
						inventory.setItem(i, player.getInventory().getArmorContents()[i]);
					}

					for(int i = 9; i < inventory.getSize(); i++) {
						inventory.setItem(i, player.getInventory().getContents()[i - 9]);
					}
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 2, 0);
	}

	@Override
	public Inventory getInventory() {
		return inventory;
	}

	public void buildInventory() {
		inventory = Bukkit.createInventory(this, 9 * 5, "Player Inventory");

		for(int i = 0; i < 4; i++) {
			inventory.setItem(i, session.getStorageProfile().armor[i]);
		}

		for(int i = 9; i < inventory.getSize(); i++) {
			inventory.setItem(i, session.getStorageProfile().cachedInventory[i - 9]);
		}

		AItemStackBuilder builder = new AItemStackBuilder(Material.ENDER_CHEST);
		builder.setName("&5View Enderchest");
		inventory.setItem(8, builder.getItemStack());
	}

	public void close() {
		HandlerList.unregisterAll(this);
		if(runnable != null) runnable.cancel();
	}


	@EventHandler
	public void onEditSessionClick(InventoryClickEvent event) {
		if(session == null || session.inventory == null) return;
		Inventory inventory = getInventory();

		if(event.getWhoClicked().getUniqueId().equals(session.getPlayerUUID()) && !playerEdit) {
			event.setCancelled(true);
		}

		if(inventory.getHolder() != this) return;

		if(event.getSlot() == 8) {
			EnderchestGUI gui = new EnderchestGUI(session.getStaffMember(), session.getPlayerUUID());
			gui.open();
		}

		if(event.getSlot() > 3 && event.getSlot() < 9) event.setCancelled(true);

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
		Inventory inventory = getInventory();

		if(event.getWhoClicked().getUniqueId().equals(session.getPlayerUUID()) && !playerEdit) {
			event.setCancelled(true);
		}

		if(inventory.getHolder() != this) return;

		if(event.getWhoClicked() == session.getStaffMember()) playerEdit = false;
		new BukkitRunnable() {
			@Override
			public void run() {
				playerEdit = true;
			}
		}.runTaskLater(PitSim.INSTANCE, 2);
	}


}
