package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.pitsim.commands.FreshCommand;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.MysticType;
import dev.kyro.pitsim.enums.PantColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class ApplyEnchantPanel extends AGUIPanel {
	public EnchantingGUI enchantingGUI;

	public ItemStack mystic;
	public Map.Entry<PitEnchant, Integer> previousEnchant;
	public int enchantSlot;

	public ApplyEnchantPanel(AGUI gui, ItemStack mystic, Map.Entry<PitEnchant, Integer> previousEnchant, int enchantSlot) {
		super(gui);
		enchantingGUI = (EnchantingGUI) gui;
		this.mystic = mystic;
		this.previousEnchant = previousEnchant;
		this.enchantSlot = enchantSlot;

		inventoryBuilder.createBorder(Material.STAINED_GLASS_PANE, 2)
				.setSlots(Material.BARRIER, 0, 45);

		List<PitEnchant> applicableEnchants = EnchantManager.getEnchants(MysticType.getMysticType(mystic));
		int count = 0;
		for(int i = 0; count != applicableEnchants.size(); i++) {

			if(i < 9 || i % 9 == 0 || i % 9 == 8) continue;

			ItemStack displayItem = FreshCommand.getFreshItem(MysticType.getMysticType(mystic), PantColor.getPantColor(mystic));
			try {
				displayItem = EnchantManager.addEnchant(displayItem, applicableEnchants.get(count++), 1, false);
			} catch(Exception ignored) { }
			getInventory().setItem(i, displayItem);
		}
	}

	@Override
	public String getName() {
		return "Choose an Enchant";
	}

	@Override
	public int getRows() {
		return 6;
	}

	@Override
	public void onClick(InventoryClickEvent event) {

		int hotbarSlot = event.getHotbarButton();
		if(hotbarSlot > 3) hotbarSlot = -1;

		int slot = event.getSlot();
		ItemStack clickedItem = event.getCurrentItem();
		Map<PitEnchant, Integer> enchantMap = EnchantManager.getEnchantsOnItem(clickedItem);
		if(event.getClickedInventory().getHolder() == this) {

			if(slot == 45) {

				openPanel(enchantingGUI.enchantingPanel);
				return;
			}

			for(Map.Entry<PitEnchant, Integer> entry : enchantMap.entrySet()) {

				try {
					if(previousEnchant != null) mystic = EnchantManager.addEnchant(mystic, previousEnchant.getKey(), 0, false);
					mystic = EnchantManager.addEnchant(mystic, entry.getKey(), hotbarSlot != -1 ? hotbarSlot : 3, false, false, enchantSlot - 1);
				} catch(Exception exception) {
					exception.printStackTrace();
				}
				enchantingGUI.updateMystic(mystic);
				openPanel(enchantingGUI.enchantingPanel);
				return;
			}
		}
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {

	}

	@Override
	public void onClose(InventoryCloseEvent event) {

	}
}
