package net.pitsim.spigot.inventories;

import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
import net.pitsim.spigot.controllers.ProxyMessaging;
import net.pitsim.spigot.controllers.objects.ServerData;
import net.pitsim.spigot.misc.HeadLib;
import net.pitsim.spigot.misc.Sounds;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

public class AdminPanel extends AGUIPanel {

	public AdminGUI adminGUI;

	public AdminPanel(AGUI gui) {
		super(gui);
		this.adminGUI = (AdminGUI) gui;
		inventoryBuilder.createBorder(Material.STAINED_GLASS_PANE, 7);

		int slotsIndex = 0;

		for(ServerData serverData : ServerData.getAllServerData()) {
			int serverIndex = serverData.index;

			String headURL = serverData.isDarkzone() ? HeadLib.getDarkzoneHead(serverIndex + 1) : HeadLib.getServerHead(serverIndex + 1);
			ItemStack head = HeadLib.getCustomHead(headURL);
			SkullMeta meta = (SkullMeta) head.getItemMeta();
			String name = serverData.isDarkzone() ? "&eDarkzone-" : "&ePitSim-";
			meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name + (serverIndex + 1) + " &7- " + (serverData.isRunning() ? "&aQUEUEABLE" : "&cNON-QUEUEABLE")));
			List<String> lore = new ArrayList<>();
			lore.add(ChatColor.GRAY + "Online Players: " + ChatColor.YELLOW + serverData.getPlayerCount());
			lore.add("");
			lore.addAll(serverData.getPlayerStrings());
			if(!serverData.getPlayerStrings().isEmpty()) lore.add("");

			if(serverData.getPlayerCount() > 0) {
				lore.add(ChatColor.YELLOW + "Left-Click to view players");
				lore.add(ChatColor.YELLOW + "Right-Click to join server");
			} else {
				lore.add(ChatColor.RED + "No Players Online!");
			}

			meta.setLore(lore);
			head.setItemMeta(meta);
			head.setAmount(serverData.getPlayerCount() > 0 ? serverData.getPlayerCount() : 1);

			getInventory().setItem((10 + slotsIndex), head);
			if(serverData.isDarkzone()) darkzoneSlots.put((10 + slotsIndex), serverIndex);
			else slots.put((10 + slotsIndex), serverIndex);
			slotsIndex++;
		}
	}

	Map<Integer, Integer> slots = new HashMap<>();
	Map<Integer, Integer> darkzoneSlots = new HashMap<>();

	@Override
	public String getName() {
		return "PitSim Servers";
	}

	@Override
	public int getRows() {
		return 3;
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		int slot = event.getSlot();
		if(event.getClickedInventory().getHolder() == this) {
			if(slots.containsKey(slot)) {
				int index = slots.get(slot);
				ServerData data = ServerData.getOverworldServerData(index);
				if(data.getPlayerCount() == 0) {
					Sounds.NO.play(event.getWhoClicked());
					return;
				}

				if(event.isLeftClick()) {
					adminGUI.serverViewPanel = new ServerViewPanel(adminGUI, data);
					openPanel(adminGUI.serverViewPanel);
					return;
				}

				if(event.isRightClick()) {

					for(String s : new ArrayList<>(data.getPlayers().keySet())) {
						if(s.equalsIgnoreCase(player.getName())) {
							AOutput.error(player, "&cYou are already in this server!");
							Sounds.NO.play(player);
							return;
						}
					}

					ProxyMessaging.switchPlayer((Player) event.getWhoClicked(), index + 1);
					return;
				}

			}

			if(darkzoneSlots.containsKey(slot)) {
				int index = darkzoneSlots.get(slot);
				ServerData data = ServerData.getDarkzoneServerData(index);
				if(data.getPlayerCount() == 0) {
					Sounds.NO.play(event.getWhoClicked());
					return;
				}

				if(event.isLeftClick()) {
					adminGUI.serverViewPanel = new ServerViewPanel(adminGUI, data);
					openPanel(adminGUI.serverViewPanel);
					return;
				}

				if(event.isRightClick()) {
					for(String s : new ArrayList<>(data.getPlayers().keySet())) {
						if(s.equalsIgnoreCase(player.getName())) {
							AOutput.error(player, "&cYou are already in this server!");
							Sounds.NO.play(player);
							return;
						}
					}

					ProxyMessaging.darkzoneSwitchPlayer((Player) event.getWhoClicked(), index + 1);
					return;
				}

			}
		}
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {

	}

	@Override
	public void onClose(InventoryCloseEvent event) {

	}
}
