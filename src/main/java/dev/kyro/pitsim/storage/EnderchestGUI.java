package dev.kyro.pitsim.storage;

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
		this.enderchestPanel = new EnderchestPanel(this, storagePlayer);
		this.wardrobePanel = new WardrobePanel(this);
		setHomePanel(enderchestPanel);
	}
}
