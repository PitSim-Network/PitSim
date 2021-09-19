package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.gui.AGUI;
import org.bukkit.entity.Player;

public class CaptchaGUI extends AGUI {

	public CaptchaGUI(Player player) {
		super(player);

		setHomePanel(new CaptchaPanel(this));
	}
}
