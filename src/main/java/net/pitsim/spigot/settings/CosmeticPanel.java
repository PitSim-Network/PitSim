package net.pitsim.spigot.settings;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import net.pitsim.spigot.cosmetics.CosmeticManager;
import net.pitsim.spigot.cosmetics.CosmeticType;
import net.pitsim.spigot.cosmetics.PitCosmetic;
import net.pitsim.spigot.misc.Misc;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CosmeticPanel extends AGUIPanel {
	public SettingsGUI settingsGUI;

	public ItemStack playerKillEffects;
	public ItemStack botKillEffects;
	public ItemStack bountyMessageItem;
	public ItemStack capesItem;
	public ItemStack particleTrailItem;
	public ItemStack auraItem;
	public ItemStack miscItem;
	public static ItemStack backItem;

	static {
		backItem = new AItemStackBuilder(Material.BARRIER)
				.setName("&cBack")
				.setLore(new ALoreBuilder(
						"&7Click to go back"
				))
				.getItemStack();
	}

	public CosmeticPanel(AGUI gui) {
		super(gui);
		settingsGUI = (SettingsGUI) gui;

		inventoryBuilder.createBorder(Material.STAINED_GLASS_PANE, 7);

		List<CosmeticType> equippedCosmeticTypes = new ArrayList<>();
		for(PitCosmetic pitCosmetic : CosmeticManager.getEquippedCosmetics(settingsGUI.pitPlayer))
			equippedCosmeticTypes.add(pitCosmetic.cosmeticType);

		playerKillEffects = new AItemStackBuilder(Material.DIAMOND_SWORD)
				.setName("&4&lPlayer Kill Effects")
				.setLore(new ALoreBuilder(
						"&7Click to pick your player kill effect"
				))
				.getItemStack();
		botKillEffects = new AItemStackBuilder(Material.IRON_SWORD)
				.setName("&c&lBot Kill Effects")
				.setLore(new ALoreBuilder(
						"&7Click to pick your bot kill effect"
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

		if(equippedCosmeticTypes.contains(CosmeticType.PLAYER_KILL_EFFECT)) Misc.addEnchantGlint(playerKillEffects);
		if(equippedCosmeticTypes.contains(CosmeticType.BOT_KILL_EFFECT)) Misc.addEnchantGlint(botKillEffects);
		if(equippedCosmeticTypes.contains(CosmeticType.BOUNTY_CLAIM_MESSAGE)) Misc.addEnchantGlint(bountyMessageItem);
		if(equippedCosmeticTypes.contains(CosmeticType.CAPE)) Misc.addEnchantGlint(capesItem);
		if(equippedCosmeticTypes.contains(CosmeticType.PARTICLE_TRAIL)) Misc.addEnchantGlint(particleTrailItem);
		if(equippedCosmeticTypes.contains(CosmeticType.AURA)) Misc.addEnchantGlint(auraItem);
		if(equippedCosmeticTypes.contains(CosmeticType.MISC)) Misc.addEnchantGlint(miscItem);

		getInventory().setItem(10, playerKillEffects);
		getInventory().setItem(11, botKillEffects);
		getInventory().setItem(12, bountyMessageItem);
		getInventory().setItem(13, capesItem);
		getInventory().setItem(14, particleTrailItem);
		getInventory().setItem(15, auraItem);
		getInventory().setItem(16, miscItem);
		getInventory().setItem(22, backItem);
	}

	@Override
	public String getName() {
		return ChatColor.YELLOW + "" + ChatColor.BOLD + "Cosmetics";
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
