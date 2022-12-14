package dev.kyro.pitsim.storage;

import dev.kyro.pitsim.controllers.objects.PluginMessage;
import org.bukkit.entity.Player;

import java.util.UUID;

public class EditSession {

	private final Player staffMember;
	private final UUID playerUUID;
	private final StorageProfile storageProfile;
	private EditType editType = null;

	private final String playerServer;
	private final boolean isPlayerOnline;

	public boolean hasResponded = false;

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

		//TODO: Open GUI here
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
