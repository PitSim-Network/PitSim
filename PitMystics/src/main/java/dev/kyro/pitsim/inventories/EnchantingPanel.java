package dev.kyro.pitsim.inventories;

import de.tr7zw.nbtapi.NBTItem;
import de.tr7zw.nbtapi.NBTList;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
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

public class EnchantingPanel extends AGUIPanel {
	public EnchantingGUI enchantingGUI;

	public EnchantingPanel(AGUI gui) {
		super(gui);
		enchantingGUI = (EnchantingGUI) gui;

		inventoryBuilder.setSlots(Material.STAINED_GLASS_PANE, 5, 0, 1, 2, 9, 11, 18, 19, 20)
				.setSlots(Material.STAINED_GLASS_PANE, 4, 3, 4, 5, 12, 14, 21, 22, 23)
				.setSlots(Material.STAINED_GLASS_PANE, 14, 6, 7, 8, 15, 17, 24, 25, 26)
				.setSlots(Material.STAINED_GLASS_PANE, 15, 27, 28, 29, 36, 38, 45, 46, 47)
				.setSlots(Material.STAINED_GLASS_PANE, 7, 30, 31, 32, 33, 34, 35, 39, 42, 44, 48, 49, 50, 51, 52, 53);

		updateInventory();
	}

	@Override
	public String getName() {
		return "Enchant GUI";
	}

	@Override
	public int getRows() {
		return 6;
	}

	@Override
	public void onClick(InventoryClickEvent event) {

		int slot = event.getSlot();
		ItemStack clickedItem = event.getCurrentItem();
		ItemStack mystic = event.getInventory().getItem(37);
		if(event.getClickedInventory().getHolder() == this) {

			if(slot == 10 || slot == 13 || slot == 16) {

				if(Misc.isAirOrNull(mystic)) {
					return;
				}

				Map.Entry<PitEnchant, Integer> displayEnchant = getDisplayEnchant(clickedItem);
				openPanel(new ApplyEnchantPanel(enchantingGUI, mystic, displayEnchant, enchantingGUI.getEnchantSlot(slot)));
//				player.openInventory(new ApplyEnchantPanel(this, displayEnchant, mystic).getInventory());
				return;
			}

			if(slot == 37) {

				getInventory().setItem(37, new ItemStack(Material.AIR));
				if(!FreshCommand.isFresh(mystic)) player.getInventory().addItem(mystic);
			}

			if(slot == 40) {

				if(Misc.isAirOrNull(mystic)) {

					getInventory().setItem(37, FreshCommand.getFreshItem(MysticType.SWORD, null));
				} else {
					AOutput.error(player, "Already an item in the mystic well");
					return;
				}
			}
			if(slot == 41) {

				if(Misc.isAirOrNull(mystic)) {

					getInventory().setItem(37, FreshCommand.getFreshItem(MysticType.BOW, null));
				} else {
					AOutput.error(player, "Already an item in the mystic well");
					return;
				}
			}
			if(slot == 43) {

				if(Misc.isAirOrNull(mystic)) {

					getInventory().setItem(37, FreshCommand.getFreshItem(MysticType.PANTS, PantColor.RED));
				} else {
					AOutput.error(player, "Already an item in the mystic well");
					return;
				}
			}
		} else {

			if(clickedItem.getType() == Material.AIR) return;
			NBTItem nbtItem = new NBTItem(clickedItem);
			if(!nbtItem.hasKey(NBTTag.ITEM_UUID.getRef())) return;

			if(!Misc.isAirOrNull(mystic)) {
				AOutput.error(player, "Already an item in the mystic well");
				return;
			}

			mystic = event.getClickedInventory().getItem(slot);
			event.getClickedInventory().setItem(slot, new ItemStack(Material.AIR));
			getInventory().setItem(37, mystic);
		}
		updateInventory();
	}

	@Override
	public void onOpen(InventoryOpenEvent event) { }

	@Override
	public void onClose(InventoryCloseEvent event) {

//		ItemStack mystic = event.getInventory().getItem(37);
//		if(!Misc.isAirOrNull(mystic) && !FreshCommand.isFresh(mystic)) {
//			player.getInventory().addItem(mystic);
//		}
	}

	@Override
	public void updateInventory() {

		ItemStack mystic = getInventory().getItem(37);
		if(Misc.isAirOrNull(mystic)) {

			getInventory().setItem(40, FreshCommand.getFreshItem(MysticType.SWORD, null));
			getInventory().setItem(41, FreshCommand.getFreshItem(MysticType.BOW, null));
			inventoryBuilder.setSlots(Material.CACTUS, 0, 43);

			inventoryBuilder.setSlots(Material.BARRIER, 0, 10, 13, 16);
		} else {

			inventoryBuilder.setSlots(Material.BARRIER, 0, 40, 41, 43);

			NBTItem nbtItem = new NBTItem(mystic);
			NBTList<String> enchantOrder = nbtItem.getStringList(NBTTag.PIT_ENCHANT_ORDER.getRef());

			for(int i = 0; i < 3; i++) {
				if(i + 1 > enchantOrder.size()) {
					ItemStack itemStack = new ItemStack(i > enchantOrder.size() ? Material.BARRIER : Material.ENCHANTMENT_TABLE);
					getInventory().setItem(10 + (3 * i), itemStack);
					continue;
				}

				ItemStack displayMystic = FreshCommand.getFreshItem(MysticType.getMysticType(mystic), PantColor.getPantColor(mystic));
				PitEnchant pitEnchant = EnchantManager.getEnchant(enchantOrder.get(i));
				assert pitEnchant != null;
				try {
					displayMystic = EnchantManager.addEnchant(displayMystic, pitEnchant, EnchantManager.getEnchantLevel(mystic, pitEnchant), false);
				} catch(Exception ignored) { }

				getInventory().setItem(10 + (3 * i), displayMystic);
			}
		}

		super.updateInventory();
	}

	public static Map.Entry<PitEnchant, Integer> getDisplayEnchant(ItemStack mystic) {

		Map<PitEnchant, Integer> enchantMap = EnchantManager.getEnchantsOnItem(mystic);
		for(Map.Entry<PitEnchant, Integer> entry : enchantMap.entrySet()) {

			return entry;
		}
		return null;
	}
}