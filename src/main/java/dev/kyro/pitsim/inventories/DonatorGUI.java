package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.gui.AGUI;
import org.bukkit.entity.Player;

public class DonatorGUI extends AGUI {

	public DonatorPanel donatorPanel;
	public PantsColorPanel pantsColorPanel;
	public ChatColorPanel chatColorPanel;
	public ChatOptionsPanel chatOptionsPanel;

	public DonatorGUI(Player player) {
		super(player);

		donatorPanel = new DonatorPanel(this);
		setHomePanel(donatorPanel);
		pantsColorPanel = new PantsColorPanel(this);
		chatColorPanel = new ChatColorPanel(this);
		chatOptionsPanel = new ChatOptionsPanel(this);
	}
}
