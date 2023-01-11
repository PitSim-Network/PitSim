package dev.kyro.pitsim.adarkzone.progression;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.gui.AGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ProgressionGUI extends AGUI {
	public static ItemStack backItem;

	public MainProgressionPanel mainProgressionPanel;

	static {
		backItem = new AItemStackBuilder(Material.BARRIER)
				.setName("&c&lBack")
				.setLore(new ALoreBuilder(
						"&7Click to go to the previous screen"
				))
				.getItemStack();
	}

	public ProgressionGUI(Player player) {
		super(player);

		this.mainProgressionPanel = new MainProgressionPanel(this);

		setHomePanel(mainProgressionPanel);
	}
}
