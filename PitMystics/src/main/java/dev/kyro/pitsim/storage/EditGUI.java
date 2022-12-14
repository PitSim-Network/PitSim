package dev.kyro.pitsim.storage;

import dev.kyro.arcticapi.gui.AGUI;
import org.bukkit.entity.Player;

public class EditGUI extends AGUI {

	public EditPanel panel;

	public EditGUI(Player player, EditSession session) {
		super(player);

		panel = new EditPanel(this, session);
		setHomePanel(panel);
	}


}
