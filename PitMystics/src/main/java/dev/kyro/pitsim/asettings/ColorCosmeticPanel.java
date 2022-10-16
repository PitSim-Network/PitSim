package dev.kyro.pitsim.asettings;

import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.RedstoneColor;
import dev.kyro.pitsim.acosmetics.PitCosmetic;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Material;
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

		inventoryBuilder.createBorder(Material.STAINED_GLASS_PANE, 7);
		for(int i = 0; i < unlockedColors.size(); i++) {
			RedstoneColor unlockedColor = unlockedColors.get(i);
			int slot = cosmeticSlots.get(i);
			colorMap.put(slot, unlockedColor);
			boolean isEquipped = pitCosmetic.isEquipped(settingsGUI.pitPlayer, unlockedColor);
			getInventory().setItem(slot, unlockedColor.getDisplayItem(isEquipped));
		}
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
			boolean success = subPanel.selectCosmetic(pitCosmetic, redstoneColor);
			if(success) {
				player.closeInventory();
				Sounds.SUCCESS.play(player);
				AOutput.send(player, "&7Equipped your " + redstoneColor.displayName + "&7 " + pitCosmetic.getDisplayName());
			}
		}
//		TODO: Back button
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {

	}

	@Override
	public void onClose(InventoryCloseEvent event) {

	}
}
