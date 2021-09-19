package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.gui.AGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EnchantingGUI extends AGUI {

	public EnchantingPanel enchantingPanel;

	public static ItemStack back;
	static {
		back = new AItemStackBuilder(new ItemStack(Material.BARRIER))
				.setName("&cBack")
				.setLore(new ALoreBuilder("&7Click to return to the", "&7previous screen"))
				.getItemStack();
	}

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
