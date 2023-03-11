package dev.kyro.pitsim.adarkzone.altar;

import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.pitsim.adarkzone.altar.pedestals.AltarPanel;
import org.bukkit.entity.Player;

public class AltarGUI extends AGUI {

	public AltarPanel altarPanel;

	public AltarGUI(Player player) {
		super(player);

		altarPanel = new AltarPanel(this);
		setHomePanel(altarPanel);
	}
}
