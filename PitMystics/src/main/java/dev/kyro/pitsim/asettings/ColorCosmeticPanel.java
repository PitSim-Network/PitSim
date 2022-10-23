package dev.kyro.pitsim.asettings;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.pitsim.ParticleColor;
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

public class ColorCosmeticPanel extends AGUIPanel {
	public SettingsGUI settingsGUI;
	public SubCosmeticPanel subPanel;
	public PitCosmetic pitCosmetic;
	public List<ParticleColor> unlockedColors;

	public static ItemStack backItem;

	public static List<Integer> cosmeticSlots = new ArrayList<>();
	public Map<Integer, ParticleColor> colorMap = new HashMap<>();

	static {
		backItem = new AItemStackBuilder(Material.BARRIER)
				.setName("&c&lBack")
				.setLore(new ALoreBuilder(
						"&7Click to go to the previous screen"
				))
				.getItemStack();

		for(int i = 9; i < 36; i++) {
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
		getInventory().setItem(getRows() * 9 - 5, backItem);

		for(int i = 0; i < unlockedColors.size(); i++) {
			ParticleColor unlockedColor = unlockedColors.get(i);
			int slot = cosmeticSlots.get(i);
			colorMap.put(slot, unlockedColor);
			boolean isEquipped = pitCosmetic.isEnabled(settingsGUI.pitPlayer, unlockedColor);
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
			ParticleColor particleColor = colorMap.get(slot);
			boolean success = subPanel.selectCosmetic(pitCosmetic, particleColor);
			if(success) {
				player.closeInventory();
				Sounds.SUCCESS.play(player);
			}
		} else if(slot == getRows() * 9 - 5) {
			openPreviousGUI();
		}
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {

	}

	@Override
	public void onClose(InventoryCloseEvent event) {

	}
}
