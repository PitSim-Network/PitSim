package dev.kyro.pitsim.storage;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import dev.kyro.pitsim.misc.PlayerItemLocation;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WardrobePanel extends AGUIPanel {
	public StorageProfile profile;

	public WardrobePanel(AGUI gui) {
		super(gui, true);
		this.profile = StorageManager.getProfile(player);
		addBackButton(getRows() * 9 - 5);
		buildInventory();
		setInventory();
		inventoryBuilder.createBorder(Material.STAINED_GLASS_PANE, 15, false);

		for(int i = 0; i < 9; i++) {
			Outfit outfit = profile.getOutfits()[i];
			Outfit.OutfitState outfitState = outfit.getState();

			addTaggedItem(i + 9, outfit::getDisplayItem, event -> {});
			if(outfit.isUnlocked()) {
				ItemStack settingsStack = new AItemStackBuilder(Material.REDSTONE_COMPARATOR)
						.setName("&2Outfit " + (outfit.getIndex() + 1) + " Settings")
						.setLore(new PitLoreBuilder(
								"&7Click to open the settings for this &2Outfit"
						))
						.getItemStack();
				addTaggedItem(i + 18, () -> settingsStack, event -> openPanel(new OutfitSettingsPanel(gui, outfit))).setItem();
			} else {
				ItemStack settingsStack = new AItemStackBuilder(Material.STAINED_GLASS_PANE, 1, 15)
						.getItemStack();
				getInventory().setItem(i + 9, settingsStack);
			}
			if(outfitState.isEquippable()) {
				addTaggedItem(i + 27, outfit::getStateItem, event -> {
					boolean success = outfit.equip(true);
					if(success) {
						AOutput.send(player, "&2&lWARDROBE!&7 Equipped &2Outfit " + (outfit.getIndex() + 1) + "&7!");
						Sounds.SUCCESS.play(player);
						player.closeInventory();
					}
				}).setItem();
			} else {
				addTaggedItem(i + 27, outfit::getStateItem, event -> {
					if(outfitState == Outfit.OutfitState.NO_DATA) {
						AOutput.error(player, "&c&lERROR!&7 No data saved to this &2Outfit&7!");
					} else if(outfitState == Outfit.OutfitState.LOCKED) {
						AOutput.error(player, "&c&lERROR!&7 You need a higher rank!");
					}
					Sounds.NO.play(player);
				}).setItem();
			}
		}

		addTaggedItem(39, () -> new AItemStackBuilder(Material.HOPPER)
				.setName("&cDump Inventory")
				.setLore(new PitLoreBuilder(
						"&7Puts your inventory and armor into your enderchest"
				)).getItemStack(), event -> {
			Map<PlayerItemLocation, ItemStack> proposedChanges = new HashMap<>();
			boolean success = profile.storeInvAndArmor(proposedChanges, new ArrayList<>(), true);
			for(Map.Entry<PlayerItemLocation, ItemStack> entry : proposedChanges.entrySet())
				entry.getKey().setItem(profile.getUniqueID(), entry.getValue(), true);
			player.updateInventory();
			if(success) {
				AOutput.send(player, "&2&lWARDROBE!&7 Moved your inventory and armor into your enderchest!");
				Sounds.SUCCESS.play(player);
			}
		});

		addTaggedItem(41, () -> new AItemStackBuilder(Material.BARRIER)
				.setName("&cClear Defaults")
				.setLore(new PitLoreBuilder(
						"&7Clears default &aOverworld &7and &5Darkzone &7sets"
				)).getItemStack(), event -> {
			profile.setDefaultOverworldSet(-1);
			profile.setDefaultDarkzoneSet(-1);
			setInventory();
			AOutput.send(player, "&2&lWARDROBE!&7 Cleared default &aOverworld &7and &5Darkzone &7sets!");
			Sounds.SUCCESS.play(player);
		});
	}

	@Override
	public void setInventory() {
		super.setInventory();
		for(TaggedItem taggedItem : taggedItemMap.values()) taggedItem.setItem();
		updateInventory();
	}

	@Override
	public String getName() {
		return "&2Wardrobe";
	}

	@Override
	public int getRows() {
		return 5;
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		if(event.getClickedInventory().getHolder() != this) return;
		int slot = event.getSlot();
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
		setInventory();
	}

	@Override
	public void onClose(InventoryCloseEvent event) {
	}
}
