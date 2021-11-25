package dev.kyro.pitsim.inventories;

import de.tr7zw.nbtapi.NBTItem;
import de.tr7zw.nbtapi.NBTList;
import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.commands.FreshCommand;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.MysticType;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.enums.PantColor;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;

public class EnchantingPanel extends AGUIPanel {
	public EnchantingGUI enchantingGUI;
	public boolean forcedClose = false;
	public int count = 0;
	public BukkitTask runnable;
	public boolean colorSelect = false;
	public ItemStack mystic = new ItemStack(Material.AIR);
	public boolean hasGivenItemBack = false;

	public static ItemStack philo;
	public static ItemStack selectEnchant;
	public static ItemStack noMystic;
	public static ItemStack noEnchantYet;
	public static ItemStack mysticInWell;
	static {
		philo = new AItemStackBuilder(new ItemStack(Material.CACTUS))
				.setName("&aPhilosopher's Cactus")
				.setLore(new ALoreBuilder("&7Click to select", "&7a pant color"))
				.getItemStack();
		selectEnchant = new AItemStackBuilder(new ItemStack(Material.ENCHANTMENT_TABLE))
				.setName("&dOpen Enchant Slot")
				.setLore(new ALoreBuilder("&7Click to add an enchant", "&7to your item"))
				.getItemStack();
		noMystic = new AItemStackBuilder(new ItemStack(Material.BARRIER))
				.setName("&cNo Item")
				.setLore(new ALoreBuilder("&7Put an item in", "&7the mystic well"))
				.getItemStack();
		noEnchantYet = new AItemStackBuilder(new ItemStack(Material.BARRIER))
				.setName("&cInvalid Enchant Slot")
				.setLore(new ALoreBuilder("&7Please click on the", "&7current slot to the left"))
				.getItemStack();
		mysticInWell = new AItemStackBuilder(new ItemStack(Material.IRON_FENCE))
				.setName("&cAlready Enchanting")
				.setLore(new ALoreBuilder("&7You cannot create a fresh", "&7item when there is already", "&7an item in the well"))
				.getItemStack();
	}

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
				if(colorSelect) return;
				InventoryView inventoryView = player.getOpenInventory();
				if(inventoryView == null) return;
				Inventory inventory = inventoryView.getTopInventory();
				if(inventory == null) return;
				if(inventory.getHolder() != enchantingPanel) return;

				if(count % 3 == 0) {
					if(count / 3 < 8) {
						getInventory().setItem(slots[count / 3], new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 10));
					} else if(count / 3 < 15) {
						getInventory().setItem(slots[(count / 3) % 8], new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15));
					} else {
						getInventory().setItem(slots[(count / 3) % 8], new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15));
						getInventory().setItem(slots[((count / 3) + 1) % 8], new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 10));
					}
				}

				updateInventory();
				count++;
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 1L);
	}

	@Override
	public String getName() {
		return "Mystic Well";
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
				if(colorSelect) return;

				if(Misc.isAirOrNull(mystic) || clickedItem.getType() != Material.ENCHANTMENT_TABLE) {
					Sounds.NO.play(player);
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
				if(colorSelect) {
					mystic = FreshCommand.getFreshItem(MysticType.PANTS, PantColor.RED);
					colorSelect = false;
				} else {
					getInventory().setItem(37, new ItemStack(Material.AIR));
					if(!FreshCommand.isFresh(mystic)) player.getInventory().addItem(mystic);
					mystic = new ItemStack(Material.AIR);
				}
			}

			if(slot == 38 && colorSelect) {
				mystic = FreshCommand.getFreshItem(MysticType.PANTS, PantColor.ORANGE);
				colorSelect = false;
			}

			if(slot == 39 && colorSelect) {
				mystic = FreshCommand.getFreshItem(MysticType.PANTS, PantColor.YELLOW);
				colorSelect = false;
			}

			if(slot == 40) {
				if(colorSelect) {
					mystic = FreshCommand.getFreshItem(MysticType.PANTS, PantColor.GREEN);
					colorSelect = false;
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
				if(colorSelect) {
					mystic = FreshCommand.getFreshItem(MysticType.PANTS, PantColor.BLUE);
					colorSelect = false;
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

					if(!colorSelect) count += 15 * 3;
					colorSelect = !colorSelect;
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
				Sounds.NO.play(player);
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
			if(!hasGivenItemBack) player.getInventory().addItem(mystic);
			hasGivenItemBack = true;
		}
		runnable.cancel();
	}

	@Override
	public void updateInventory() {
		if(Misc.isAirOrNull(mystic)) {

			if(colorSelect) {
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
//			inventoryBuilder.setSlots(Material.CACTUS, 0, 43);
//			inventoryBuilder.setSlots(Material.BARRIER, 0, 10, 13, 16);

			getInventory().setItem(43, philo);
			getInventory().setItem(10, noMystic);
			getInventory().setItem(13, noMystic);
			getInventory().setItem(16, noMystic);
		} else {
			if(getInventory().getItem(38).getType() != Material.STAINED_GLASS_PANE) {
				inventoryBuilder.setSlots(Material.STAINED_GLASS_PANE, 7, 39);
				inventoryBuilder.setSlots(Material.STAINED_GLASS_PANE, 15, 27, 28, 29, 36, 38, 45, 46, 47);
			}

			getInventory().setItem(37, mystic);

//			inventoryBuilder.setSlots(Material.BARRIER, 0, 40, 41, 43);
			getInventory().setItem(40, mysticInWell);
			getInventory().setItem(41, mysticInWell);
			getInventory().setItem(43, mysticInWell);

			NBTItem nbtItem = new NBTItem(mystic);
			NBTList<String> enchantOrder = nbtItem.getStringList(NBTTag.PIT_ENCHANT_ORDER.getRef());

			for(int i = 0; i < 3; i++) {
				if(i + 1 > enchantOrder.size()) {
					ItemStack itemStack = i > enchantOrder.size() ? noEnchantYet : selectEnchant;
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

		makeSectionActive(getActiveSection());
		super.updateInventory();
	}

	public static Map.Entry<PitEnchant, Integer> getDisplayEnchant(ItemStack mystic) {

		Map<PitEnchant, Integer> enchantMap = EnchantManager.getEnchantsOnItem(mystic);
		for(Map.Entry<PitEnchant, Integer> entry : enchantMap.entrySet()) {

			return entry;
		}
		return null;
	}

	public GUISection getActiveSection() {

		if(Misc.isAirOrNull(mystic)) return colorSelect ? GUISection.COLOR_SELECT : GUISection.FRESH_SELECT;
		if(getInventory().getItem(10).getType() == Material.ENCHANTMENT_TABLE) return GUISection.TIER_1;
		if(getInventory().getItem(13).getType() == Material.ENCHANTMENT_TABLE) return GUISection.TIER_2;
		if(getInventory().getItem(16).getType() == Material.ENCHANTMENT_TABLE) return GUISection.TIER_3;
		return GUISection.MYSTIC_INPUT;
	}

	public void makeSectionActive(GUISection guiSection) {
		for(GUISection value : GUISection.values()) {
			if(value == guiSection) continue;
			for(int slot : value.slots) {
				ItemStack itemStack = getInventory().getItem(slot);
				if(Misc.isAirOrNull(itemStack)) continue;
				if(itemStack.getType() == Material.STAINED_GLASS_PANE) itemStack.removeEnchantment(Enchantment.DURABILITY);
				getInventory().setItem(slot, itemStack);
			}
		}
		for(int slot : guiSection.slots) {
			ItemStack itemStack = getInventory().getItem(slot);
			if(Misc.isAirOrNull(itemStack) || itemStack.getType() == Material.LEATHER_LEGGINGS) continue;
			itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
			ItemMeta itemMeta = itemStack.getItemMeta(); itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS); itemStack.setItemMeta(itemMeta);
			getInventory().setItem(slot, itemStack);
		}
	}

	private enum GUISection {
		TIER_1(0, 1, 2, 9, 11, 18, 19, 20),
		TIER_2(3, 4, 5, 12, 14, 21, 22, 23),
		TIER_3(6, 7, 8, 15, 17, 24, 25, 26),
		MYSTIC_INPUT(27, 28, 29, 36, 38, 45, 46, 47),
		FRESH_SELECT(30, 31, 32, 33, 34, 35, 39, 42, 44, 48, 49, 50, 51, 52, 53),
		COLOR_SELECT(27, 28, 29, 36, 38, 45, 46, 47, 30, 31, 32, 33, 34, 35, 39, 42, 44, 48, 49, 50, 51, 52, 53);

		public int[] slots;

		GUISection(int... slots) {
			this.slots = slots;
		}
	}
}