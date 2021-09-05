package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.gui.AGUI;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EnchantingGUI extends AGUI {

	public EnchantingPanel enchantingPanel;

	public EnchantingGUI(Player player) {
		super(player);

		enchantingPanel = new EnchantingPanel(this);
		setHomePanel(enchantingPanel);
	}

	public void updateMystic(ItemStack mystic) {

		enchantingPanel.mystic = mystic;
		enchantingPanel.updateInventory();
	}

	public int getEnchantSlot(int clickedSlot) {

		return (clickedSlot - 7) / 3;
	}
}
