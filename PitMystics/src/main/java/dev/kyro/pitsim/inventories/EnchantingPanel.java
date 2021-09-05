package dev.kyro.pitsim.inventories;

import de.tr7zw.nbtapi.NBTItem;
import de.tr7zw.nbtapi.NBTList;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.ASound;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.commands.FreshCommand;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.MysticType;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.enums.PantColor;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;

public class EnchantingPanel extends AGUIPanel {
	public EnchantingGUI enchantingGUI;
	public boolean forcedClose = false;
	public int count = 0;
	public BukkitTask runnable;
	public boolean freshSelect = false;
	public ItemStack mystic = new ItemStack(Material.AIR);

	public EnchantingPanel(AGUI gui) {
		super(gui);
		enchantingGUI = (EnchantingGUI) gui;

		inventoryBuilder.setSlots(Material.STAINED_GLASS_PANE, 5, 0, 1, 2, 9, 11, 18, 19, 20)
				.setSlots(Material.STAINED_GLASS_PANE, 4, 3, 4, 5, 12, 14, 21, 22, 23)
				.setSlots(Material.STAINED_GLASS_PANE, 14, 6, 7, 8, 15, 17, 24, 25, 26)
				.setSlots(Material.STAINED_GLASS_PANE, 15, 27, 28, 29, 36, 38, 45, 46, 47)
				.setSlots(Material.STAINED_GLASS_PANE, 7, 30, 31, 32, 33, 34, 35, 39, 42, 44, 48, 49, 50, 51, 52, 53);

		EnchantingPanel enchantingPanel = this;
		int[] slots = new int[] { 27, 28, 29, 38, 47, 46, 45, 36 };
		runnable = new BukkitRunnable() {
			@Override
			public void run() {
				if(freshSelect) return;
				InventoryView inventoryView = player.getOpenInventory();
				if(inventoryView == null) return;
				Inventory inventory = inventoryView.getTopInventory();
				if(inventory == null) return;
				if(inventory.getHolder() != enchantingPanel) return;

				if(count < 8) {
					getInventory().setItem(slots[count], new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 10));
				} else if(count < 15) {
					getInventory().setItem(slots[count % 8], new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15));
				} else {
					getInventory().setItem(slots[count % 8], new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15));
					getInventory().setItem(slots[(count + 1) % 8], new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 10));
				}
//				getInventory().setItem(slots[((count + 5) % 4) + 4], new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 10));
//				getInventory().setItem(slots[(count % 4) + 4], new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15));

				updateInventory();
				count++;
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 3L);
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
		if(event.getClickedInventory().getHolder() == this) {

			if(slot == 10 || slot == 13 || slot == 16) {
				if(freshSelect) return;

				if(Misc.isAirOrNull(mystic) || clickedItem.getType() != Material.ENCHANTMENT_TABLE) {
					ASound.play(player, Sound.VILLAGER_NO);
					return;
				}

				Map.Entry<PitEnchant, Integer> displayEnchant = getDisplayEnchant(clickedItem);

				NBTItem nbtItem = new NBTItem(mystic);
				if(displayEnchant != null && nbtItem.getString(NBTTag.ITEM_JEWEL_ENCHANT.getRef()).equals(displayEnchant.getKey().refNames.get(0))) {
					AOutput.error(player, "You cannot modify a jewel enchant");
					return;
				}

				forcedClose = true;
				openPanel(new ApplyEnchantPanel(enchantingGUI, mystic, displayEnchant, enchantingGUI.getEnchantSlot(slot)));
				return;
			}

			if(slot == 37) {
				if(freshSelect) {
					mystic = FreshCommand.getFreshItem(MysticType.PANTS, PantColor.RED);
					freshSelect = false;
				} else {
					getInventory().setItem(37, new ItemStack(Material.AIR));
					if(!FreshCommand.isFresh(mystic)) player.getInventory().addItem(mystic);
					mystic = new ItemStack(Material.AIR);
				}
			}

			if(slot == 38 && freshSelect) {
				mystic = FreshCommand.getFreshItem(MysticType.PANTS, PantColor.ORANGE);
				freshSelect = false;
			}

			if(slot == 39 && freshSelect) {
				mystic = FreshCommand.getFreshItem(MysticType.PANTS, PantColor.YELLOW);
				freshSelect = false;
			}

			if(slot == 40) {
				if(freshSelect) {
					mystic = FreshCommand.getFreshItem(MysticType.PANTS, PantColor.GREEN);
					freshSelect = false;
				} else {
					if(Misc.isAirOrNull(mystic)) {

						mystic = FreshCommand.getFreshItem(MysticType.SWORD, null);
					} else {
						AOutput.error(player, "Already an item in the mystic well");
						return;
					}
				}
			}
			if(slot == 41) {
				if(freshSelect) {
					mystic = FreshCommand.getFreshItem(MysticType.PANTS, PantColor.BLUE);
					freshSelect = false;
				} else {
					if(Misc.isAirOrNull(mystic)) {

						mystic = FreshCommand.getFreshItem(MysticType.BOW, null);
					} else {
						AOutput.error(player, "Already an item in the mystic well");
						return;
					}
				}
			}
			if(slot == 43) {
				if(Misc.isAirOrNull(mystic)) {

					freshSelect = !freshSelect;
					updateInventory();
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

			if(EnchantManager.isJewel(clickedItem) && !EnchantManager.isJewelComplete(clickedItem)) {
				AOutput.error(player, "You cannot enchant incomplete jewels");
				ASound.play(player, Sound.VILLAGER_NO);
				return;
			}

			mystic = event.getClickedInventory().getItem(slot);
			event.getClickedInventory().setItem(slot, new ItemStack(Material.AIR));
		}
		updateInventory();
	}

	@Override
	public void onOpen(InventoryOpenEvent event) { }

	@Override
	public void onClose(InventoryCloseEvent event) {
		if(!forcedClose) closeGUI();
	}

	public void closeGUI() {
		if(!Misc.isAirOrNull(mystic) && !FreshCommand.isFresh(mystic)) {
			player.getInventory().addItem(mystic);
		}
		runnable.cancel();
	}

	@Override
	public void updateInventory() {
		if(Misc.isAirOrNull(mystic)) {

			if(freshSelect) {
				for(int i = 0; i < 5; i++) {
					ItemStack fresh = FreshCommand.getFreshItem(MysticType.PANTS, PantColor.values()[i]);
					getInventory().setItem(i + 37, fresh);
				}
				inventoryBuilder.setSlots(Material.STAINED_GLASS_PANE, 7, 27, 28, 29, 36, 45, 46, 47);
			} else {
				if(getInventory().getItem(38).getType() != Material.STAINED_GLASS_PANE) {
					inventoryBuilder.setSlots(Material.STAINED_GLASS_PANE, 7, 39);
					inventoryBuilder.setSlots(Material.STAINED_GLASS_PANE, 15, 27, 28, 29, 36, 38, 45, 46, 47);
				}

				getInventory().setItem(37, mystic);
				getInventory().setItem(40, FreshCommand.getFreshItem(MysticType.SWORD, null));
				getInventory().setItem(41, FreshCommand.getFreshItem(MysticType.BOW, null));
			}
			inventoryBuilder.setSlots(Material.CACTUS, 0, 43);
			inventoryBuilder.setSlots(Material.BARRIER, 0, 10, 13, 16);
		} else {
			if(getInventory().getItem(38).getType() != Material.STAINED_GLASS_PANE) {
				inventoryBuilder.setSlots(Material.STAINED_GLASS_PANE, 7, 39);
				inventoryBuilder.setSlots(Material.STAINED_GLASS_PANE, 15, 27, 28, 29, 36, 38, 45, 46, 47);
			}

			getInventory().setItem(37, mystic);

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
