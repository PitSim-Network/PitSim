package dev.kyro.pitsim.inventories;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.ProxyMessaging;
import dev.kyro.pitsim.controllers.objects.ServerData;
import dev.kyro.pitsim.misc.HeadLib;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
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
			ItemStack head = getCustomHead(headURL);
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
		return "Admin Menu";
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

	public ItemStack getCustomHead(String url) {

		ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
		SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
		Field field = null;

		assert skullMeta != null;

		if(url.length() < 16) {

			skullMeta.setOwner(url);

			skull.setItemMeta(skullMeta);
			return skull;
		}

		StringBuilder s_url = new StringBuilder();
		s_url.append("https://textures.minecraft.net/texture/").append(url); // We get the texture link.

		GameProfile gameProfile = new GameProfile(UUID.randomUUID(), null); // We create a GameProfile

		// We get the bytes from the texture in Base64 encoded that comes from the Minecraft-URL.
		byte[] data = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", s_url.toString()).getBytes());

		// We set the texture property in the GameProfile.
		gameProfile.getProperties().put("textures", new Property("textures", new String(data)));

		try {

			field = skullMeta.getClass().getDeclaredField("profile"); // We get the field profile.

			field.setAccessible(true); // We set as accessible to modify.
			field.set(skullMeta, gameProfile); // We set in the skullMeta the modified GameProfile that we created.

		} catch(Exception e) {
			e.printStackTrace();
		}

		skull.setItemMeta(skullMeta);

		return skull; //Finally, you have the custom head!

	}
}
