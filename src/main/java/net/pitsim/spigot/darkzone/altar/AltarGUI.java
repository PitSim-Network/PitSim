package net.pitsim.spigot.darkzone.altar;

import dev.kyro.arcticapi.gui.AGUI;
import net.pitsim.spigot.darkzone.altar.pedestals.AltarPanel;
import org.bukkit.entity.Player;

public class AltarGUI extends AGUI {

	public AltarPanel altarPanel;

	public AltarGUI(Player player) {
		super(player);

		altarPanel = new AltarPanel(this);
		setHomePanel(altarPanel);
	}
}
