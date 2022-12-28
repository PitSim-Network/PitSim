package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.gui.AGUI;
import org.bukkit.entity.Player;

public class GemGUI extends AGUI {
	public TotallyLegitGemPanel totallyLegitGemPanel;

	public GemGUI(Player player) {
		super(player);

		totallyLegitGemPanel = new TotallyLegitGemPanel(this);
		setHomePanel(totallyLegitGemPanel);
	}
}
