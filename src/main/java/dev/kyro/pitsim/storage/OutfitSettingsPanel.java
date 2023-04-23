package dev.kyro.pitsim.storage;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class OutfitSettingsPanel extends AGUIPanel {
	public StorageProfile profile;
	public Outfit outfit;

	public OutfitSettingsPanel(AGUI gui, Outfit outfit) {
		super(gui, true);
		this.profile = StorageManager.getProfile(player);
		this.outfit = outfit;
		addBackButton(getRows() * 9 - 5);
		buildInventory();
		setInventory();
		inventoryBuilder.createBorder(Material.STAINED_GLASS_PANE, 15, false);
		inventoryBuilder.setSlots(Material.STAINED_GLASS_PANE, 15, 11, 12, 13, 14, 15, 16);

		Outfit.OutfitState outfitState = outfit.getState();

		addTaggedItem(10, () -> new AItemStackBuilder(Material.DIAMOND_BLOCK)
				.setName("&aSave Outfit")
				.setLore(new PitLoreBuilder(
						"&7Saves your current inventory and armor to this &2Outfit"
				))
				.getItemStack(), event -> {
			outfit.save();
			AOutput.send(player, "&2&lWARDROBE!&2 Outfit " + (outfit.getIndex() + 1) + " &7saved!");
			Sounds.SUCCESS.play(player);
			openPreviousGUI();
		}).setItem();

		addTaggedItem(11, () -> new AItemStackBuilder(Material.HOPPER)
				.setName("&cClear Outfit")
				.setLore(new PitLoreBuilder(
						"&7Clears this &2Outfit"
				))
				.getItemStack(), event -> {
			outfit.clear();
			AOutput.send(player, "&2&lWARDROBE!&2 Outfit " + (outfit.getIndex() + 1) + " &7cleared!");
			Sounds.SUCCESS.play(player);
			openPreviousGUI();
		}).setItem();

		addTaggedItem(13, () -> new AItemStackBuilder(outfit.getDisplayItem())
				.setName("&eSet Display Item")
				.setLore(new PitLoreBuilder(
						"&7Click to set the display item to the item that you are holding"
				))
				.getItemStack(), event -> {
			outfit.setDisplayItem(player.getItemInHand().clone());
			AOutput.send(player, "&2&lWARDROBE!&7 Updated the display item for &2Outfit " + (outfit.getIndex() + 1) + "&7!");
			Sounds.SUCCESS.play(player);
			openPreviousGUI();
		}).setItem();

		ALoreBuilder setOverworldLore = new ALoreBuilder();
		Consumer<InventoryClickEvent> setOverworldClick = event -> {
			profile.setDefaultOverworldSet(outfit.getIndex());
			AOutput.send(player, "&2&lOUTFIT!&7 Set your default &aOverworld &2Outfit &7to &2Outfit " + (outfit.getIndex() + 1));
			Sounds.SUCCESS.play(player);
			openPreviousGUI();
		};
		if(!outfitState.isEquippable()) {
			setOverworldLore.addLore(
					"&cCannot set an empty outfit",
					"&7as a default"
			);
			setOverworldClick = null;
		} else if(profile.getDefaultOverworldSet() == -1) {
			setOverworldLore.addLore("&7Current: None!");
		} else if(profile.getDefaultOverworldSet() == outfit.getIndex()) {
			setOverworldLore.addLore("&7Current: This one!");
			setOverworldClick = null;
		} else {
			setOverworldLore.addLore("&7Current: &2Outfit " + (profile.getDefaultOverworldSet() + 1));
		}
		ItemStack setOverworldBaseStack = new ItemStack(Material.STAINED_CLAY, 1, (short) 13);
		if(!outfitState.isEquippable()) setOverworldBaseStack.setType(Material.BARRIER);
		addTaggedItem(15, () -> new AItemStackBuilder(setOverworldBaseStack)
				.setName("&7Set &7Default &aOverworld &2Outfit")
				.setLore(setOverworldLore)
				.getItemStack(), setOverworldClick).setItem();

		ALoreBuilder setDarkzoneLore = new ALoreBuilder();
		Consumer<InventoryClickEvent> setDarkzoneClick = event -> {
			profile.setDefaultOverworldSet(outfit.getIndex());
			AOutput.send(player, "&2&lOUTFIT!&7 Set your default &5Darkzone &2Outfit &7to &2Outfit " + (outfit.getIndex() + 1));
			Sounds.SUCCESS.play(player);
			openPreviousGUI();
		};
		if(!outfitState.isEquippable()) {
			setDarkzoneLore.addLore(
					"&cCannot set an empty outfit",
					"&7as a default"
			);
			setDarkzoneClick = null;
		} else if(profile.getDefaultOverworldSet() == -1) {
			setDarkzoneLore.addLore("&7Current: None!");
		} else if(profile.getDefaultOverworldSet() == outfit.getIndex()) {
			setDarkzoneLore.addLore("&7Current: This one!");
			setDarkzoneClick = null;
		} else {
			setDarkzoneLore.addLore("&7Current: &2Outfit " + (profile.getDefaultOverworldSet() + 1));
		}
		ItemStack setDarkzoneBaseStack = new ItemStack(Material.STAINED_CLAY, 1, (short) 10);
		if(!outfitState.isEquippable()) setDarkzoneBaseStack.setType(Material.BARRIER);
		addTaggedItem(16, () -> new AItemStackBuilder(setDarkzoneBaseStack)
				.setName("&7Set &7Default &5Darkzone &2Outfit")
				.setLore(setDarkzoneLore)
				.getItemStack(), setDarkzoneClick).setItem();
	}

	@Override
	public String getName() {
		return "&2Outfit Settings";
	}

	@Override
	public int getRows() {
		return 3;
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		if(event.getClickedInventory().getHolder() != this) return;
		int slot = event.getSlot();
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
	}

	@Override
	public void onClose(InventoryCloseEvent event) {
	}
}
