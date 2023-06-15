package net.pitsim.spigot.adarkzone;

import dev.kyro.arcticapi.gui.AGUI;
import net.pitsim.spigot.adarkzone.abilities.FastTravelPanel;
import org.bukkit.entity.Player;

public class FastTravelGUI extends AGUI {

	public FastTravelPanel fastTravelPanel;
	public FastTravelGUI(Player player) {
		super(player);

		fastTravelPanel = new FastTravelPanel(this);

		setHomePanel(fastTravelPanel);
	}
}
