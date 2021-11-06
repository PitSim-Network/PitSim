package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.gui.AGUI;
import org.bukkit.entity.Player;

public class BoosterGUI extends AGUI {

	public BoosterPanel boosterPanel;

	public BoosterGUI(Player player) {
		super(player);

		boosterPanel = new BoosterPanel(this);
		setHomePanel(boosterPanel);

	}

}
