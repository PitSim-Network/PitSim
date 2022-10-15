package dev.kyro.pitsim.asettings;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.pitsim.acosmetics.CosmeticManager;
import dev.kyro.pitsim.acosmetics.CosmeticType;
import dev.kyro.pitsim.acosmetics.PitCosmetic;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class SubCosmeticPanel extends AGUIPanel {
	public SettingsGUI settingsGUI;

	public static ItemStack previousPageItem;
	public static ItemStack nextPageItem;
	public static ItemStack backItem;

	public CosmeticType cosmeticType;
	public int slot;

	public int page;
	public static List<Integer> cosmeticSlots = new ArrayList<>();
	public Map<Integer, PitCosmetic> cosmeticMap = new HashMap<>();

	static {
		previousPageItem = new AItemStackBuilder(Material.PAPER)
				.setName("&f&lPrevious Page")
				.setLore(new ALoreBuilder(
						"&7Click to view the previous page"
				))
				.getItemStack();

		nextPageItem = new AItemStackBuilder(Material.PAPER)
				.setName("&f&lNext Page")
				.setLore(new ALoreBuilder(
						"&7Click to view the next page"
				))
				.getItemStack();

		backItem = new AItemStackBuilder(Material.BARRIER)
				.setName("&c&lBack")
				.setLore(new ALoreBuilder(
						"&7Click to go to the previous screen"
				))
				.getItemStack();

		for(int i = 10; i < 45; i++) {
			if(i % 9 == 0 || (i + 1) % 9 == 0) continue;
			cosmeticSlots.add(i);
		}
	}

	public SubCosmeticPanel(AGUI gui, CosmeticType cosmeticType) {
		super(gui, true);
		settingsGUI = (SettingsGUI) gui;
		this.cosmeticType = cosmeticType;
		this.slot = cosmeticType.getSettingsGUISlot();
		buildInventory();

		inventoryBuilder.createBorder(Material.STAINED_GLASS_PANE, 7);

		getInventory().setItem(getRows() * 9 - 5, backItem);
		if(getPages() != 1) {
			getInventory().setItem(getRows() * 9 - 9, previousPageItem);
			getInventory().setItem(getRows() * 9 - 1, nextPageItem);
		}

//		TODO: create a list of slots that are possible to assign cosmetics to (save globally for recalling on click)
		for(int i = 10; i < settingsGUI.getRows(this) * 9 - 9; i++) {
			if(i % 9 == 0 || (i + 1) % 9 == 0) continue;
			cosmeticMap.put(i, null);
		}

		setPage(1);
	}

//	TODO: Implement
	public void selectCosmetic(int slot) {
		PitCosmetic cosmetic = cosmeticMap.get(slot);
		if(cosmetic == null) return;


	}

	public void setPage(int page) {
		this.page = page;

		List<PitCosmetic> unlockedCosmetics = CosmeticManager.getUnlockedCosmetics(settingsGUI.pitPlayer, cosmeticType);
		int cosmeticListSize = unlockedCosmetics.size();
		for(int i = cosmeticSlots.size() * (page - 1); i < cosmeticSlots.size() * page; i++) {
			int slot = cosmeticSlots.get(i % cosmeticSlots.size());
			if(i >= cosmeticListSize) {
				cosmeticMap.put(slot, null);
				continue;
			}

			PitCosmetic pitCosmetic = unlockedCosmetics.get(i);
			cosmeticMap.put(slot, pitCosmetic);
			getInventory().setItem(slot, pitCosmetic.getDisplayItem(pitCosmetic.isEnabled(settingsGUI.pitPlayer)));
		}
	}

	@Override
	public String getName() {
		return cosmeticType.getPanelName();
	}

	@Override
	public int getRows() {
		SettingsGUI settingsGUI = (SettingsGUI) gui;
		return settingsGUI.getRows(this);
	}

	public int getPages() {
		return settingsGUI.getPages(this);
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		if(event.getClickedInventory().getHolder() != this) return;
		int slot = event.getSlot();

		if(slot == getRows() * 9 - 9) {
			if(page > 1) {
				setPage(page - 1);
			} else {
				Sounds.NO.play(player);
			}
		} else if(slot == getRows() * 9 - 5) {
			openPreviousGUI();
		} else if(slot == getRows() * 9 - 1) {
			if(page < settingsGUI.getPages(this)) {
				setPage(page + 1);
			} else {
				Sounds.NO.play(player);
			}
		}
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {}

	@Override
	public void onClose(InventoryCloseEvent event) {}
}
