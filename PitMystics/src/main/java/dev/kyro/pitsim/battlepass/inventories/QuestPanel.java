package dev.kyro.pitsim.battlepass.inventories;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.pitsim.battlepass.PassManager;
import dev.kyro.pitsim.battlepass.PassQuest;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuestPanel extends AGUIPanel {
	public PassGUI passGUI;

	public static ItemStack backItem;

	public int dailyQuestPage = 1;
	public List<Integer> dailyQuestSlots = Arrays.asList(10, 11, 12, 19, 20, 21, 28, 29, 30, 37, 38, 39);

	public int weeklyQuestPage = 1;
	public List<Integer> weeklyQuestSlots = Arrays.asList(14, 15, 16, 23, 24, 25, 32, 33, 34, 41, 42, 43);

	static {
		backItem = new AItemStackBuilder(Material.BARRIER)
				.setName("&c&lBack")
				.setLore(new ALoreBuilder(
						"&7Click to go to the previous screen"
				))
				.getItemStack();
	}

	public QuestPanel(AGUI gui) {
		super(gui);
		passGUI = (PassGUI) gui;

		inventoryBuilder.createBorder(Material.STAINED_GLASS_PANE, 7)
				.setSlots(Material.STAINED_GLASS_PANE, 7, 13, 22, 31, 40);

		getInventory().setItem(4, PassPanel.purchaseItem);
		getInventory().setItem(49, backItem);
	}

	public void setDailyQuestPage(int page) {
		ItemStack dailyQuestItem = new AItemStackBuilder(Material.PAPER)
				.setName("&e&lDaily Quests")
				.setLore(new ALoreBuilder(
						"&7Active daily quests",
						"",
						"&7Page: &3" + dailyQuestPage + "&7/&3" + getDailyPages()
				))
				.getItemStack();
		getInventory().setItem(2, dailyQuestItem);

		List<PassQuest> fullList = PassManager.getDailyQuests();
		fullList.removeIf(passQuest -> !passQuest.canProgressQuest(passGUI.pitPlayer));
		List<PassQuest> displayList = new ArrayList<>();
		for(int i = 0; i < dailyQuestSlots.size() + 1; i++) {
			int index = i + (page - 1) * dailyQuestSlots.size();
			if(index >= fullList.size()) break;
			displayList.add(fullList.get(index));
		}

		for(int i = 0; i < dailyQuestSlots.size(); i++) {
			int slot = dailyQuestSlots.get(i);
			if(i < displayList.size()) {
				PassQuest toDisplay = displayList.get(i);

				ItemStack itemStack = toDisplay.getDisplayItem(passGUI.pitPlayer, toDisplay.getDailyState(),
						PassManager.getProgression(passGUI.pitPlayer, toDisplay));
				ItemMeta itemMeta = itemStack.getItemMeta();
				itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
				itemStack.setItemMeta(itemMeta);
				getInventory().setItem(slot, itemStack);
			} else {
				ItemStack itemStack = new ItemStack(Material.AIR);
				getInventory().setItem(slot, itemStack);
			}
		}
	}

	public void setWeeklyQuestPage(int page) {
		ItemStack weeklyQuestItem = new AItemStackBuilder(Material.BOOK)
				.setName("&e&lWeekly Quests")
				.setLore(new ALoreBuilder(
						"&7Active weekly quests",
						"",
						"&7Page: &3" + weeklyQuestPage + "&7/&3" + getWeeklyPages()
				))
				.getItemStack();
		getInventory().setItem(6, weeklyQuestItem);

		List<PassQuest> fullList = PassManager.getWeeklyQuests();
		fullList.removeIf(passQuest -> !passQuest.canProgressQuest(passGUI.pitPlayer));
		List<PassQuest> displayList = new ArrayList<>();
		for(int i = 0; i < weeklyQuestSlots.size() + 1; i++) {
			int index = i + (page - 1) * weeklyQuestSlots.size();
			if(index >= fullList.size()) break;
			displayList.add(fullList.get(index));
		}

		for(int i = 0; i < weeklyQuestSlots.size(); i++) {
			int slot = weeklyQuestSlots.get(i);
			if(i < displayList.size()) {
				PassQuest toDisplay = displayList.get(i);

				ItemStack itemStack = toDisplay.getDisplayItem(passGUI.pitPlayer, PassManager.currentPass.weeklyQuests.get(toDisplay),
						PassManager.getProgression(passGUI.pitPlayer, toDisplay));
				ItemMeta itemMeta = itemStack.getItemMeta();
				itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
				itemStack.setItemMeta(itemMeta);
				getInventory().setItem(slot, itemStack);
			} else {
				ItemStack itemStack = new ItemStack(Material.AIR);
				getInventory().setItem(slot, itemStack);
			}
		}
	}

	public int getDailyPages() {
		List<PassQuest> dailyQuests = PassManager.getDailyQuests();
		dailyQuests.removeIf(passQuest -> !passQuest.canProgressQuest(passGUI.pitPlayer));
		return dailyQuests.size() / dailyQuestSlots.size() + 1;
	}

	public int getWeeklyPages() {
		List<PassQuest> weeklyQuests = PassManager.getWeeklyQuests();
		weeklyQuests.removeIf(passQuest -> !passQuest.canProgressQuest(passGUI.pitPlayer));
		return weeklyQuests.size() / weeklyQuestSlots.size() + 1;
	}

	@Override
	public String getName() {
		return "" + ChatColor.GOLD + ChatColor.BOLD + "Pit" + ChatColor.YELLOW + ChatColor.BOLD + "Sim " + ChatColor.DARK_AQUA + ChatColor.BOLD + "Quests";
	}

	@Override
	public int getRows() {
		return 6;
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		if(event.getClickedInventory().getHolder() != this) return;
		int slot = event.getSlot();

		if(slot == 2) {

		} else if(slot == 6) {
			if(weeklyQuestPage < getWeeklyPages()) {
				weeklyQuestPage++;
			} else {
				weeklyQuestPage = 1;
			}
			setWeeklyQuestPage(weeklyQuestPage);
		} else if(slot == 49) {
			openPreviousGUI();
		}
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
		setDailyQuestPage(dailyQuestPage);
		setWeeklyQuestPage(weeklyQuestPage);
	}

	@Override
	public void onClose(InventoryCloseEvent event) {

	}
}
