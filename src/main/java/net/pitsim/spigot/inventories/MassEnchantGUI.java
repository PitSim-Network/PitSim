package net.pitsim.spigot.inventories;

import dev.kyro.arcticapi.gui.AGUI;
import org.bukkit.entity.Player;

public class MassEnchantGUI extends AGUI {

	public String mysticType;
	public MassEnchantPanel panel;

	public MassEnchantGUI(Player player, String mysticType) {
		super(player);

		this.mysticType = mysticType;
		this.panel = new MassEnchantPanel(this);
		setHomePanel(panel);
	}
}
