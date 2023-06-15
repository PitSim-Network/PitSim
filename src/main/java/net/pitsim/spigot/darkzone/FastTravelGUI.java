package net.pitsim.spigot.darkzone;

import dev.kyro.arcticapi.gui.AGUI;
import net.pitsim.spigot.darkzone.abilities.FastTravelPanel;
import org.bukkit.entity.Player;

public class FastTravelGUI extends AGUI {

	public FastTravelPanel fastTravelPanel;
	public FastTravelGUI(Player player) {
		super(player);

		fastTravelPanel = new FastTravelPanel(this);

		setHomePanel(fastTravelPanel);
	}
}
