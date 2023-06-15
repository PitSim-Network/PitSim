package net.pitsim.spigot.storage;

import dev.kyro.arcticapi.gui.AGUI;
import org.bukkit.entity.Player;

import java.util.*;

public class EnderchestGUI extends AGUI {
	public UUID storagePlayer;

	public EnderchestPanel enderchestPanel;
	public WardrobePanel wardrobePanel;

	public EnderchestGUI(Player openPlayer, UUID storagePlayer) {
		super(openPlayer);

		this.storagePlayer = storagePlayer;
		StorageProfile storageProfile = StorageManager.getProfile(storagePlayer);
		this.enderchestPanel = new EnderchestPanel(this, storageProfile);
		this.wardrobePanel = new WardrobePanel(this);
		setHomePanel(enderchestPanel);
	}
}
