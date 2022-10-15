package dev.kyro.pitsim.asettings;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.pitsim.acosmetics.CosmeticType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class CosmeticPanel extends AGUIPanel {
	public SettingsGUI settingsGUI;

	public static ItemStack killEffectItem;
	public static ItemStack deathEffectItem;
	public static ItemStack bountyMessageItem;
	public static ItemStack capesItem;
	public static ItemStack particleTrailItem;
	public static ItemStack auraItem;
	public static ItemStack miscItem;
	public static ItemStack backItem;

	static {
		killEffectItem = new AItemStackBuilder(Material.DIAMOND_SWORD)
				.setName("&c&lKill Effects")
				.setLore(new ALoreBuilder(
						"&7Click to pick your kill effect"
				))
				.getItemStack();
		deathEffectItem = new AItemStackBuilder(Material.SKULL_ITEM, 1, 1)
				.setName("&9&lDeath Effects")
				.setLore(new ALoreBuilder(
						"&7Click to pick your death effect"
				))
				.getItemStack();
		bountyMessageItem = new AItemStackBuilder(Material.GOLD_INGOT)
				.setName("&6&lBounty Messages")
				.setLore(new ALoreBuilder(
						"&7Click to pick your bounty claim message"
				))
				.getItemStack();
		capesItem = new AItemStackBuilder(Material.BANNER, 1, 15)
				.setName("&f&lCapes")
				.setLore(new ALoreBuilder(
						"&7Click to pick your cape"
				))
				.getItemStack();
		particleTrailItem = new AItemStackBuilder(Material.FIREWORK)
				.setName("&e&lParticle Trails")
				.setLore(new ALoreBuilder(
						"&7Click to pick your particle trail"
				))
				.getItemStack();
		auraItem = new AItemStackBuilder(Material.BEACON)
				.setName("&a&lAuras")
				.setLore(new ALoreBuilder(
						"&7Click to pick your aura"
				))
				.getItemStack();
		miscItem = new AItemStackBuilder(Material.LAVA_BUCKET)
				.setName("&e&lMisc")
				.setLore(new ALoreBuilder(
						"&7Click to pick a misc cosmetic"
				))
				.getItemStack();
		backItem = new AItemStackBuilder(Material.BARRIER)
				.setName("&c&lBack")
				.setLore(new ALoreBuilder(
						"&7Click to go back"
				))
				.getItemStack();
	}

	public CosmeticPanel(AGUI gui) {
		super(gui);
		settingsGUI = (SettingsGUI) gui;

		inventoryBuilder.createBorder(Material.STAINED_GLASS_PANE, 7);

		getInventory().setItem(10, killEffectItem);
		getInventory().setItem(11, deathEffectItem);
		getInventory().setItem(12, bountyMessageItem);
		getInventory().setItem(13, capesItem);
		getInventory().setItem(14, particleTrailItem);
		getInventory().setItem(15, auraItem);
		getInventory().setItem(16, miscItem);
		getInventory().setItem(22, backItem);
	}

	@Override
	public String getName() {
		return ChatColor.YELLOW + "Cosmetics";
	}

	@Override
	public int getRows() {
		return 3;
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		if(event.getClickedInventory().getHolder() != this) return;
		int slot = event.getSlot();

		for(Map.Entry<CosmeticType, SubCosmeticPanel> entry : settingsGUI.cosmeticPanelMap.entrySet()) {
			if(slot != entry.getKey().getSettingsGUISlot()) continue;
			openPanel(entry.getValue());
		}

		if(slot == 22) {
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
