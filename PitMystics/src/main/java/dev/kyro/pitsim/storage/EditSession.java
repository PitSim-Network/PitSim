package dev.kyro.pitsim.storage;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PluginMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class EditSession {

	private final Player staffMember;
	private final UUID playerUUID;
	private final StorageProfile storageProfile;
	private EditType editType = null;

	private final String playerServer;
	private final boolean isPlayerOnline;

	public boolean hasResponded = false;

	protected Inventory inventory = null;

	public EditSession(Player staffMember, UUID playerUUID, String playerServer, boolean isPlayerOnline) {
		this.staffMember = staffMember;
		this.playerUUID = playerUUID;

		this.storageProfile = StorageManager.getProfile(playerUUID);
		this.playerServer = playerServer;
		this.isPlayerOnline = isPlayerOnline;

		EditGUI gui = new EditGUI(staffMember, this);
		gui.open();
	}

	protected void respond(EditType editType) {
		this.editType = editType;

		PluginMessage response = new PluginMessage().writeString("EDIT RESPONSE");
		response.writeString(staffMember.getUniqueId().toString());
		response.writeString(editType.message);
		response.writeBoolean(editType == EditType.OFFLINE_KICK);

		response.send();
		hasResponded = true;

		if(editType == EditType.CANCELED) {
			end();
			return;
		}

		new BukkitRunnable() {
			@Override
			public void run() {
				createInventory();
			}
		}.runTaskLater(PitSim.INSTANCE, 10);


	}

	public void end() {
		StorageManager.editSessions.remove(this);
		if(editType == EditType.OFFLINE || editType == EditType.OFFLINE_KICK) storageProfile.saveData();

		PluginMessage message = new PluginMessage().writeString("EDIT SESSION END");
		message.writeString(playerUUID.toString()).send();
	}

	private void createInventory() {
		inventory = Bukkit.createInventory(null, 9 * 5, "Player Inventory");

		for(int i = 0; i < 4; i++) {
			inventory.setItem(i, storageProfile.armor[i]);
		}

		for(int i = 9; i < inventory.getSize(); i++) {
			inventory.setItem(i, storageProfile.cachedInventory[i - 9]);
		}

		AItemStackBuilder builder = new AItemStackBuilder(Material.ENDER_CHEST);
		builder.setName("&5View Enderchest");
		inventory.setItem(8, builder.getItemStack());

		staffMember.openInventory(inventory);
	}

	public Player getStaffMember() {
		return staffMember;
	}

	public UUID getPlayerUUID() {
		return playerUUID;
	}

	public StorageProfile getStorageProfile() {
		return storageProfile;
	}

	public EditType getEditType() {
		return editType;
	}

	public String getPlayerServer() {
		return playerServer;
	}

	public boolean isPlayerOnline() {
		return isPlayerOnline;
	}
}

enum EditType {
	ONLINE("ONLINE"),
	OFFLINE("OFFLINE"),
	OFFLINE_KICK("OFFLINE"),
	CANCELED("CANCEL");

	public final String message;
	EditType(String message) {
		this.message = message;
	}
}
