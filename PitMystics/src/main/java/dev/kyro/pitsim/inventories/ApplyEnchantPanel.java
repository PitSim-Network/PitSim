package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.pitsim.commands.FreshCommand;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enchants.SelfCheckout;
import dev.kyro.pitsim.enums.MysticType;
import dev.kyro.pitsim.enums.PantColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ApplyEnchantPanel extends AGUIPanel {
	public EnchantingGUI enchantingGUI;

	public ItemStack mystic;
	public Map.Entry<PitEnchant, Integer> previousEnchant;
	public int enchantSlot;
	public boolean forcedClose = false;

	public ApplyEnchantPanel(AGUI gui, ItemStack mystic, Map.Entry<PitEnchant, Integer> previousEnchant, int enchantSlot) {
		super(gui);
		enchantingGUI = (EnchantingGUI) gui;
		this.mystic = mystic;
		this.previousEnchant = previousEnchant;
		this.enchantSlot = enchantSlot;

		inventoryBuilder.createBorder(Material.STAINED_GLASS_PANE, 2);
//				.setSlots(Material.BARRIER, 0, 45);
		getInventory().setItem(49, EnchantingGUI.back);

		Map<PitEnchant, Integer> enchantMap = EnchantManager.getEnchantsOnItem(mystic);
		if(previousEnchant != null) enchantMap.remove(previousEnchant.getKey());

		int enchants = enchantMap.size();
		boolean hasCommon = EnchantManager.isJewel(mystic);
		for(Map.Entry<PitEnchant, Integer> entry : EnchantManager.getEnchantsOnItem(mystic).entrySet()) {
			if(!entry.getKey().isUncommonEnchant) hasCommon = true;
		}
		List<PitEnchant> applicableEnchants = new ArrayList<>();
		for(PitEnchant enchant : EnchantManager.getEnchants(MysticType.getMysticType(mystic))) {
			if((!hasCommon || (previousEnchant != null && !previousEnchant.getKey().isUncommonEnchant)) && enchants >= 2 && enchant.isUncommonEnchant) continue;
			if(EnchantManager.getEnchantsOnItem(mystic).containsKey(enchant)) continue;
			if(enchant.getClass() == SelfCheckout.class) continue;
			applicableEnchants.add(enchant);
		}
		int count = 0;
		for(int i = 0; count != applicableEnchants.size(); i++) {

			if(i < 9 || i % 9 == 0 || i % 9 == 8) continue;

			ItemStack displayItem = FreshCommand.getFreshItem(MysticType.getMysticType(mystic), PantColor.getPantColor(mystic));
			try {
				displayItem = EnchantManager.addEnchant(displayItem, applicableEnchants.get(count++), 3, false);
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

			if(slot == 49) {

				forcedClose = true;
				openPanel(enchantingGUI.enchantingPanel);
				enchantingGUI.enchantingPanel.forcedClose = false;
				return;
			}

			for(Map.Entry<PitEnchant, Integer> entry : enchantMap.entrySet()) {

				try {
//					mystic = EnchantManager.addEnchant(mystic, entry.getKey(), hotbarSlot != -1 ? hotbarSlot : 3, false, false, enchantSlot - 1);
				} catch(Exception exception) {
					exception.printStackTrace();
				}
				forcedClose = true;
				openPanel(new ApplyEnchantLevelPanel(enchantingGUI, mystic, entry.getKey(), enchantSlot, previousEnchant));
				return;
			}
		}
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {

	}

	@Override
	public void onClose(InventoryCloseEvent event) {
		if(!forcedClose) enchantingGUI.enchantingPanel.closeGUI();
	}
}
