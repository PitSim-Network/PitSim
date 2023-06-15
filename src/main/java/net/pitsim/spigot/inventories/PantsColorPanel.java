package net.pitsim.spigot.inventories;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import net.pitsim.spigot.items.MysticFactory;
import net.pitsim.spigot.controllers.objects.PitPlayer;
import net.pitsim.spigot.enums.MysticType;
import net.pitsim.spigot.enums.NBTTag;
import net.pitsim.spigot.enums.PantColor;
import net.pitsim.spigot.misc.Misc;
import net.pitsim.spigot.misc.Sounds;
import net.pitsim.spigot.settings.SettingsGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public  class PantsColorPanel extends AGUIPanel {
	PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

	public SettingsGUI settingsGUI;

	public PantsColorPanel(AGUI gui) {
		super(gui);
		settingsGUI = (SettingsGUI) gui;
	}

	@Override
	public String getName() {
		return "Pants Colorizer";
	}

	@Override
	public int getRows() {
		return 5;
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		int slot = event.getSlot();
		if(event.getClickedInventory().getHolder() == this) {

			if(slot == 40) {
				openPanel(settingsGUI.getHomePanel());
			}

			if(Misc.isAirOrNull(getInventory().getItem(slot)) || !getInventory().getItem(slot).getType().equals(Material.LEATHER_LEGGINGS))
				return;
			if(MysticFactory.isFresh(player.getInventory().getLeggings()) || Misc.isAirOrNull(player.getInventory().getLeggings()))
				return;
			if(!player.hasPermission("pitsim.pantscolor")) return;

			PantColor pantColor = PantColor.getPantColor(getInventory().getItem(slot));

			PantColor originalPantColor = PantColor.getPantColor(player.getInventory().getLeggings());
			assert originalPantColor != null;

			if(originalPantColor == PantColor.RED || originalPantColor == PantColor.BLUE ||
					originalPantColor == PantColor.GREEN || originalPantColor == PantColor.YELLOW || originalPantColor == PantColor.ORANGE) {

				NBTItem nbtMystic = new NBTItem(player.getInventory().getLeggings());
				nbtMystic.setString(NBTTag.ORIGINAL_PANTS_COLOR.getRef(), originalPantColor.displayName);

				player.getInventory().setLeggings(nbtMystic.getItem());

			}

			if(slot == 41) {
				NBTItem nbtMystic = new NBTItem(player.getInventory().getLeggings());
				nbtMystic.removeKey(NBTTag.ORIGINAL_PANTS_COLOR.getRef());

				player.getInventory().setLeggings(nbtMystic.getItem());
			}

			for(PantColor matchingPantColor : PantColor.values()) {
				if(matchingPantColor.equals(pantColor)) {
					player.getInventory().setLeggings(PantColor.setPantColor(player.getInventory().getLeggings(), pantColor));
					Sounds.SUCCESS.play(player);
					player.closeInventory();
				}
			}

			updateInventory();
		}
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {

		int i = 9;

		for(PantColor pantColor : PantColor.values()) {
			if(pantColor.displayName.equals("Blue") || pantColor.displayName.equals("Red") || pantColor.displayName.equals("Orange")
					|| pantColor.displayName.equals("Yellow") || pantColor.displayName.equals("Green") || pantColor.displayName.equals("Jewel")
					|| pantColor.displayName.equals("Dark") || pantColor.displayName.equals(""))
				continue;

			ItemStack pants = MysticFactory.getFreshItem(MysticType.PANTS, pantColor);
			ItemMeta meta = pants.getItemMeta();
			meta.setDisplayName(ChatColor.GOLD + "Premium Color");
			List<String> pantslore = new ArrayList<>();
			pantslore.add(ChatColor.translateAlternateColorCodes('&', "&7Color: " + pantColor.chatColor + pantColor.displayName));
			pantslore.add(ChatColor.translateAlternateColorCodes('&', "&7Hex: " + pantColor.hexColor));
			pantslore.add("");
			if(player.getInventory().getLeggings() == null || player.getInventory().getLeggings().getType() != Material.LEATHER_LEGGINGS || !player.getInventory().getLeggings().hasItemMeta()) {
				pantslore.add(ChatColor.GRAY + "Wearing: " + ChatColor.RED + "None!");
			} else if(Objects.equals(PantColor.getPantColor(player.getInventory().getLeggings()), PantColor.JEWEL) || MysticFactory.isFresh(player.getInventory().getLeggings())) {
				pantslore.add(ChatColor.GRAY + "Wearing: " + ChatColor.RED + "Undyeable pants!");
			} else {
				pantslore.add(ChatColor.GRAY + "Wearing: " + player.getInventory().getLeggings().getItemMeta().getDisplayName());
			}
			pantslore.add("");
			pantslore.add(ChatColor.YELLOW + "Click to apply dye!");
			meta.setLore(pantslore);
			pants.setItemMeta(meta);
			pants = PantColor.setPantColor(pants, pantColor);
			getInventory().setItem(i, pants);
			i++;
		}

		ItemStack playerPants = player.getInventory().getLeggings();
		PantColor originalColor = getOriginalColor(playerPants);

		if(originalColor != null) {

			ItemStack original = MysticFactory.getFreshItem(MysticType.PANTS, originalColor);
			ItemMeta originalMeta = original.getItemMeta();
			originalMeta.setDisplayName(ChatColor.GOLD + "Original Color");
			List<String> originalLore = new ArrayList<>();
			originalLore.add(ChatColor.translateAlternateColorCodes('&', "&7Color: "
					+ originalColor.chatColor + getOriginalColor(playerPants).displayName));
			originalLore.add(ChatColor.GRAY + "Hex: " + getOriginalColor(playerPants).hexColor);
			originalLore.add("");
			originalLore.add(ChatColor.GRAY + "Wearing: " + playerPants.getItemMeta().getDisplayName());
			originalLore.add("");
			originalLore.add(ChatColor.YELLOW + "Click to add dye!");
			originalMeta.setLore(originalLore);
			original.setItemMeta(originalMeta);

			original = PantColor.setPantColor(original, originalColor);

			getInventory().setItem(41, original);
		}

		ItemStack back = new ItemStack(Material.ARROW);
		ItemMeta backmeta = back.getItemMeta();
		backmeta.setDisplayName(ChatColor.GREEN + "Go Back");
		List<String> backlore = new ArrayList<>();
		backlore.add(ChatColor.GRAY + "To Donator Perks");
		backmeta.setLore(backlore);
		back.setItemMeta(backmeta);

		getInventory().setItem(40, back);

	}

	public PantColor getOriginalColor(ItemStack item) {
		if(Misc.isAirOrNull(item)) return null;
		ItemMeta itemMeta = item.getItemMeta();
		if(itemMeta == null || !itemMeta.hasLore()) return null;

		NBTItem nbtItem = new NBTItem(item);

		if(!nbtItem.hasKey(NBTTag.ORIGINAL_PANTS_COLOR.getRef())) return null;

		return PantColor.getPantColor(nbtItem.getString(NBTTag.ORIGINAL_PANTS_COLOR.getRef()));
	}

	@Override
	public void onClose(InventoryCloseEvent event) {

	}
}
