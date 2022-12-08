package storage;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PluginMessage;
import dev.kyro.pitsim.events.MessageEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StorageManager implements Listener {
	private static final List<StorageProfile> profiles = new ArrayList<>();

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

	public static StorageProfile getInitialProfile(UUID uuid) {
		StorageProfile profile = new StorageProfile(uuid, 18);
		profiles.add(profile);

		return profile;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onJoin(PlayerJoinEvent event) {
		if(PitSim.getStatus() == PitSim.ServerStatus.ALL) return;

		Player player = event.getPlayer();
		StorageProfile profile = getProfile(player);

		if(!profile.hasData()) {
			player.kickPlayer(ChatColor.RED + "An error occurred when loading your data. Please report this issue.");
			return;
		}

		player.getInventory().setContents(profile.getCachedInventory());
		player.getInventory().setArmorContents(profile.getArmor());
		player.updateInventory();
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onQuit(PlayerQuitEvent event) {
		if(PitSim.getStatus() == PitSim.ServerStatus.ALL) return;

		Player player = event.getPlayer();
		StorageProfile profile = getProfile(player);

		profile.saveEnderchest();
		profile.saveInventory();

		profiles.remove(profile);
	}

	@EventHandler
	public void onPluginMessage(MessageEvent event) {
		PluginMessage message = event.getMessage();
		List<String> strings = message.getStrings();

		if(strings.size() < 2) return;

		if(strings.get(0).equals("ENDERCHEST SAVE") || strings.get(0).equals("INVENTORY SAVE")) {
			System.out.println("Save confirm attempt");
			System.out.println(strings.get(1));
			UUID uuid = UUID.fromString(strings.get(1));
			Player player = Bukkit.getPlayer(uuid);
			if(player == null) return;
			System.out.println(2);

			StorageProfile profile = getProfile(player);
			if(!profile.hasData()) return;
			System.out.println(3);

			profile.receiveSaveConfirmation(message);
			System.out.println(4);
		}

		if(strings.get(0).equals("LOAD REQUEST")) {
			System.out.println("Load!");
			UUID uuid = UUID.fromString(strings.get(1));

			StorageProfile profile = getInitialProfile(uuid);
			System.out.println(profile);
		}

		if(strings.get(0).equals("ENDERCHEST")) {
			UUID uuid = UUID.fromString(strings.get(1));

			StorageProfile profile = getProfile(uuid);
			message.getStrings().remove(0);
			message.getStrings().remove(0);
			profile.setEnderchest(message);
		}

		if(strings.get(0).equals("INVENTORY")) {
			UUID uuid = UUID.fromString(strings.get(1));

			StorageProfile profile = getProfile(uuid);
			message.getStrings().remove(0);
			message.getStrings().remove(0);
			profile.setInventory(message);
		}
	}

	@EventHandler
	public void onPickup(PlayerPickupItemEvent event) {
		Player player = event.getPlayer();
		StorageProfile profile = getProfile(player);
		if(profile.hasData() && profile.isSaving()) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPickup(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		StorageProfile profile = getProfile(player);
		if(profile.hasData() && profile.isSaving()) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onClick(InventoryDragEvent event) {
		Player player = (Player) event.getWhoClicked();
		StorageProfile profile = getProfile(player);
		if(profile.hasData() && profile.isSaving()) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		StorageProfile profile = getProfile(player);
		if(profile.hasData() && profile.isSaving()) {
			event.setCancelled(true);
		}
	}


}
