package dev.kyro.pitsim.storage;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PluginMessage;
import dev.kyro.pitsim.events.MessageEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class StorageManager implements Listener {
	public static final int MAX_ENDERCHEST_PAGES = 18;
	public static final int ENDERCHEST_ITEM_SLOTS = 36;
	public static final int OUTFITS = 9;

	protected static final List<StorageProfile> profiles = new ArrayList<>();
	protected static final List<EditSession> editSessions = new ArrayList<>();

	public static StorageProfile getProfile(Player player) {
		for(StorageProfile profile : profiles) {
			if(profile.getUUID().equals(player.getUniqueId())) return profile;
		}

		StorageProfile profile = new StorageProfile(player.getUniqueId());
		profiles.add(profile);
		return profile;
	}

	public static StorageProfile getProfile(UUID uuid) {
		for(StorageProfile profile : profiles) {
			if(profile.getUUID().equals(uuid)) return profile;
		}

		StorageProfile profile = new StorageProfile(uuid);
		profiles.add(profile);

		return profile;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onJoin(PlayerJoinEvent event) {
		if(PitSim.getStatus() == PitSim.ServerStatus.STANDALONE) return;

		Player player = event.getPlayer();
		StorageProfile profile = getProfile(player);

//		Disabled because the deletion/clear method was disabled
//		if(hasItem || !Misc.isAirOrNull(player.getInventory().getHelmet()) ||
//				!Misc.isAirOrNull(player.getInventory().getChestplate()) ||
//				!Misc.isAirOrNull(player.getInventory().getLeggings()) ||
//				!Misc.isAirOrNull(player.getInventory().getBoots())) {
//			Misc.alertDiscord("@everyone " + player.getName() + " logged in to server " + PitSim.serverName + " with items in their inventory");
//		}

		if(!profile.isLoaded()) {
			player.kickPlayer(ChatColor.RED + "An error occurred when loading your data. Please report this issue.");
			return;
		}

		player.setItemOnCursor(null);
		player.getInventory().setContents(profile.getInventory());
		player.getInventory().setArmorContents(profile.getArmor());
		player.updateInventory();
	}

	public static void quitInitiate(Player player) {
		StorageProfile profile = getProfile(player);

		profile.setInventory(player.getInventory().getContents());
		profile.setArmor(player.getInventory().getArmorContents());
	}

	public static void quitCleanup(Player player) {
		if(PitSim.getStatus() == PitSim.ServerStatus.STANDALONE) return;

		StorageProfile profile = getProfile(player);

		if(!isBeingEdited(player.getUniqueId())) profiles.remove(profile);
//		File file = new File("world/playerdata/" + player.getUniqueId().toString() + ".dat");
//		file.delete();

//		player.getInventory().clear();
//		player.getInventory().setArmorContents(new ItemStack[] {new ItemStack(Material.AIR), new ItemStack(Material.AIR),
//		new ItemStack(Material.AIR), new ItemStack(Material.AIR)});
	}

	public static boolean isEditing(Player player) {
		for(EditSession editSession : editSessions) {
			if(editSession.getStaffMember() == player) return true;
		}
		return false;
	}

	public static boolean isEditing(UUID uuid) {
		for(EditSession editSession : editSessions) {
			if(editSession.getStaffMember().getUniqueId().equals(uuid)) return true;
		}
		return false;
	}

	public static boolean isBeingEdited(UUID uuid) {
		for(EditSession editSession : editSessions) {
			if(editSession.getPlayerUUID().equals(uuid)) return true;
		}
		return false;
	}

	public static EditSession getSession(Player staff) {
		for(EditSession editSession : editSessions) {
			if(editSession.getStaffMember() == staff) return editSession;
		}
		throw new RuntimeException();
	}

	public static EditSession getSession(UUID playerUUID) {
		for(EditSession editSession : editSessions) {
			if(editSession.getPlayerUUID().equals(playerUUID)) return editSession;
		}
		return null;
	}

	@EventHandler
	public void onPluginMessage(MessageEvent event) {
		PluginMessage message = event.getMessage();
		List<String> strings = message.getStrings();

		if(strings.size() < 2) return;

		if(strings.get(0).equals("SAVE CONFIRMATION")) {
			UUID uuid = UUID.fromString(strings.get(1));

			StorageProfile profile = getProfile(uuid);

			profile.receiveSaveConfirmation(message);
		} else if(strings.get(0).equals("PLAYER DATA")) {
			strings.remove(0);
			UUID uuid = UUID.fromString(strings.remove(0));

			StorageProfile profile = getProfile(uuid);
			profile.loadData(message);
		} else if(strings.get(0).equals("PROMPT EDIT MENU")) {
			UUID staffUUID = UUID.fromString(strings.get(1));

			Player player = Bukkit.getPlayer(staffUUID);
			if(player == null) return;

			UUID playerUUID = UUID.fromString(strings.get(2));
			boolean isOnline = message.getBooleans().get(0);
			String serverName = strings.get(3);

			for(EditSession editSession : editSessions) {
				if(editSession.getPlayerUUID() != playerUUID) continue;

				editSession.delete();
				Misc.alertDiscord("@everyone EDIT SESSION CREATION ERROR! " + playerUUID + " already existed in edit session list");
			}

			new BukkitRunnable() {
				@Override
				public void run() {
					editSessions.add(new EditSession(player, playerUUID, serverName, isOnline));
				}
			}.runTask(PitSim.INSTANCE);
		}
	}

	@EventHandler
	public void onPickup(PlayerPickupItemEvent event) {
		Player player = event.getPlayer();
		StorageProfile profile = getProfile(player);
		if(profile.isLoaded() && profile.isSaving()) event.setCancelled(true);
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		StorageProfile profile = getProfile(player);
		if(profile.isLoaded() && profile.isSaving()) event.setCancelled(true);
	}

	@EventHandler
	public void onClick(InventoryDragEvent event) {
		Player player = (Player) event.getWhoClicked();
		StorageProfile profile = getProfile(player);
		if(profile.isLoaded() && profile.isSaving()) event.setCancelled(true);
	}

	@EventHandler
	public void onClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		int slot = event.getSlot();

		StorageProfile profile;
		if(isEditing(player)) {
			profile = Objects.requireNonNull(getSession(player)).getStorageProfile();
		} else profile = getProfile(player);

		if(profile.isLoaded() && profile.isSaving()) {
			event.setCancelled(true);
			return;
		}

		for(EnderchestPage enderchestPage : profile.getEnderchestPages()) {
			Inventory inventory = enderchestPage.getInventory();
			if(!inventory.equals(event.getClickedInventory())) continue;

			if(slot == ENDERCHEST_ITEM_SLOTS + 9 && enderchestPage.getIndex() > 0) {
				if(isEditing(player)) getSession(player).playerClosed = false;
				player.openInventory(profile.getEnderchestPage(enderchestPage.getIndex() - 1).getInventory());
				if(isEditing(player)) getSession(player).playerClosed = true;
				event.setCancelled(true);
			} else if(slot == ENDERCHEST_ITEM_SLOTS + 17 && enderchestPage.getIndex() + 1 < MAX_ENDERCHEST_PAGES) {
				EnderchestGUI.EnderchestPages rank = EnderchestGUI.EnderchestPages.getRank(player);
				if(enderchestPage.getIndex() + 1 >= rank.pages && !isEditing(player)) continue;
				if(isEditing(player)) getSession(player).playerClosed = false;
				player.openInventory(profile.getEnderchestPage(enderchestPage.getIndex() + 1).getInventory());
				if(isEditing(player)) getSession(player).playerClosed = true;
			} else if(slot == ENDERCHEST_ITEM_SLOTS + 13) {
				if(isEditing(player)) getSession(player).playerClosed = false;
				new EnderchestGUI(player, profile.getUUID()).open();
				if(isEditing(player)) getSession(player).playerClosed = true;
			} else if(slot < 9 || slot > 35) {
//				Does not run the else return
			} else {
				return;
			}

			event.setCancelled(true);
			player.updateInventory();
		}
	}

	@EventHandler
	public void onClose(InventoryCloseEvent event) {
		Player player = (Player) event.getPlayer();
		if(!isEditing(player)) return;
		EditSession session = getSession(player);
		StorageProfile profile = session.getStorageProfile();

		for(EnderchestPage enderchestPage : profile.getEnderchestPages()) {
			if(!enderchestPage.getInventory().equals(event.getInventory())) continue;
			if(session.playerClosed) session.end();
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		EditSession endSession = null;
		for(EditSession editSession : new ArrayList<>(editSessions)) {
			if(editSession.getPlayerUUID().equals(player.getUniqueId()) && editSession.getEditType() == EditType.ONLINE) {
				endSession = editSession;
				AOutput.error(editSession.getStaffMember(), "&cYour session ended because the player logged out!");
				editSession.getStaffMember().closeInventory();
			}
			if(editSession.getStaffMember() == player) endSession = editSession;
		}
		if(endSession != null) endSession.end();
	}
}
