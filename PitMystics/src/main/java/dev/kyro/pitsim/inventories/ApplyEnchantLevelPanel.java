package dev.kyro.pitsim.inventories;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.pitsim.commands.FreshCommand;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.MysticType;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.enums.PantColor;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class ApplyEnchantLevelPanel extends AGUIPanel {
	public EnchantingGUI enchantingGUI;

	public ItemStack mystic;
	public PitEnchant enchant;
	public int enchantSlot;
	public Map.Entry<PitEnchant, Integer> previousEnchant;
	public boolean forcedClose = false;

	public static ItemStack tooPowerful;
	static {
		tooPowerful = new AItemStackBuilder(new ItemStack(Material.BARRIER))
				.setName("&cToo Powerful")
				.setLore(new ALoreBuilder("&7You have too many enchant tokens", "&7of this type on your item"))
				.getItemStack();
	}

	public ApplyEnchantLevelPanel(AGUI gui, ItemStack mystic, PitEnchant enchant, int enchantSlot, Map.Entry<PitEnchant, Integer> previousEnchant) {
		super(gui);
		enchantingGUI = (EnchantingGUI) gui;
		this.mystic = mystic;
		this.enchant = enchant;
		this.enchantSlot = enchantSlot;
		this.previousEnchant = previousEnchant;

		inventoryBuilder.setSlots(Material.STAINED_GLASS_PANE, 5, 0, 1, 2, 9, 11, 18, 19, 20)
				.setSlots(Material.STAINED_GLASS_PANE, 4, 3, 4, 5, 12, 14, 21, 22, 23)
				.setSlots(Material.STAINED_GLASS_PANE, 14, 6, 7, 8, 15, 17, 24, 25, 26);
//		getInventory().setItem(22, EnchantingGUI.back);

		for(int i = 0; i < 3; i++) {
			getInventory().setItem(10 + i * 3, tooPowerful);

			NBTItem nbtItem = new NBTItem(mystic);
			if(nbtItem.getInteger(NBTTag.ITEM_TOKENS.getRef()) + i + 1 > 8) continue;
			if(enchant.isRare && nbtItem.getInteger(NBTTag.ITEM_RTOKENS.getRef()) + i + 1 > 4) continue;

			ItemStack displayItem = FreshCommand.getFreshItem(MysticType.getMysticType(mystic), PantColor.getPantColor(mystic));
			try {
				displayItem = EnchantManager.addEnchant(displayItem, enchant, i + 1, false);
			} catch(Exception ignored) { }
			getInventory().setItem(10 + i * 3, displayItem);
		}

		updateInventory();
	}

	@Override
	public String getName() {
		return "Choose an Level";
	}

	@Override
	public int getRows() {
		return 3;
	}

	@Override
	public void onClick(InventoryClickEvent event) {

		int slot = event.getSlot();
		ItemStack clickedItem = event.getCurrentItem();
		Map<PitEnchant, Integer> enchantMap = EnchantManager.getEnchantsOnItem(clickedItem);
		if(event.getClickedInventory().getHolder() == this) {

//			if(slot == 22) {
//
//				forcedClose = true;
//				openPanel(previousGUI);w
//				((ApplyEnchantPanel) previousGUI).forcedClose = false;
//				return;
//			}

			if(Misc.isAirOrNull(clickedItem) || clickedItem.equals(tooPowerful)) return;

			int applyLvl;
			if(slot == 10) {
				applyLvl = 1;
			} else if(slot == 13) {
				applyLvl = 2;
			} else if(slot == 16) {
				applyLvl = 3;
			} else return;
			try {
				if(previousEnchant != null) mystic = EnchantManager.addEnchant(mystic, previousEnchant.getKey(), 0, false);
				mystic = EnchantManager.addEnchant(mystic, enchant, applyLvl, false, false, enchantSlot - 1);
			} catch(Exception ignored) { }
			enchantingGUI.updateMystic(mystic);
			forcedClose = true;
			openPanel(enchantingGUI.enchantingPanel);
			enchantingGUI.enchantingPanel.forcedClose = false;
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
