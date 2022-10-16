package dev.kyro.pitsim.asettings;

import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.pitsim.RedstoneColor;
import dev.kyro.pitsim.acosmetics.PitCosmetic;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ColorCosmeticPanel extends AGUIPanel {
	public SettingsGUI settingsGUI;
	public SubCosmeticPanel subPanel;
	public PitCosmetic pitCosmetic;
	public List<RedstoneColor> unlockedColors;

	public static List<Integer> cosmeticSlots = new ArrayList<>();
	public Map<Integer, RedstoneColor> colorMap = new HashMap<>();

	static {
		for(int i = 9; i < 27; i++) {
			if(i % 9 == 0 || (i + 1) % 9 == 0) continue;
			cosmeticSlots.add(i);
		}
	}

	public ColorCosmeticPanel(AGUI gui, SubCosmeticPanel subPanel, PitCosmetic pitCosmetic) {
		super(gui, true);
		this.settingsGUI = (SettingsGUI) gui;
		this.subPanel = subPanel;
		this.pitCosmetic = pitCosmetic;
		this.unlockedColors = pitCosmetic.getUnlockedColors(settingsGUI.pitPlayer);
		buildInventory();

		for(int i = 0; i < RedstoneColor.values().length; i++) {
			RedstoneColor redstoneColor = RedstoneColor.values()[i];
			colorMap.put(i, redstoneColor);
		}

//		TODO: Actually set items (requires items to be generated)
	}

	@Override
	public String getName() {
		return pitCosmetic.getDisplayName();
	}

	@Override
	public int getRows() {
		return (unlockedColors.size() - 1) / 7 + 3;
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		if(event.getClickedInventory().getHolder() != this) return;
		int slot = event.getSlot();

		if(colorMap.containsKey(slot)) {
			RedstoneColor redstoneColor = colorMap.get(slot);
			subPanel.selectCosmetic(pitCosmetic, redstoneColor);
		}
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {

	}

	@Override
	public void onClose(InventoryCloseEvent event) {

	}
}
