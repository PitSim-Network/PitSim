package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.pitsim.enums.MysticType;
import org.bukkit.entity.Player;

public class TestEnchantGUI extends AGUI {

	public String mysticType;
	public TestEnchantPanel panel;

	public TestEnchantGUI(Player player, String mysticType) {
		super(player);

		this.mysticType = mysticType;
		this.panel = new TestEnchantPanel(this);
		setHomePanel(panel);
	}
}
