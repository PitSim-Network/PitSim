package dev.kyro.pitsim.inventories;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.pitsim.controllers.objects.PluginMessage;
import dev.kyro.pitsim.controllers.objects.ServerData;
import dev.kyro.pitsim.misc.HeadLib;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.*;

public class KeeperPanel extends AGUIPanel {
	public KeeperPanel(AGUI gui) {
		super(gui);

		inventoryBuilder.createBorder(Material.STAINED_GLASS_PANE, 7);
	}

	Map<Integer, Integer> slots = new HashMap<>();

	@Override
	public String getName() {
		return "Change Lobbies";
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
				new PluginMessage().writeString("QUEUE").writeString(String.valueOf(((Player) event.getWhoClicked()).getName())).writeInt(slots.get(slot) + 1).send();
			}
		}
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
		List<Integer> slots = getSlots(ServerData.getServerCount());
		int slotsIndex = 0;


		for(Map.Entry<Integer, ServerData> entry : ServerData.servers.entrySet()) {
			int serverIndex = entry.getKey();
			ServerData serverData = entry.getValue();

			String headURL = HeadLib.getServerHead(serverIndex + 1);
			ItemStack head = getCustomHead(headURL);
			SkullMeta meta = (SkullMeta) head.getItemMeta();
			if(serverData.isRunning()) {
				meta.setDisplayName(ChatColor.GREEN + "PitSim-" + (serverIndex + 1));
			} else {
				meta.setDisplayName(ChatColor.RED + "PitSim-" + (serverIndex + 1));
			}
			List<String> lore = new ArrayList<>();
			lore.add(ChatColor.GRAY + "Online Players: " + ChatColor.YELLOW + serverData.getPlayerCount());

			if(serverData.isRunning()) {
				lore.add("");
				lore.addAll(serverData.getPlayerStrings());
			}
			lore.add("");
			if(serverData.isRunning()) {
				lore.add(ChatColor.GREEN + "Click to Join!");
			} else {
				lore.add(ChatColor.RED + "Server is Unavailable!");
			}

			meta.setLore(lore);
			head.setItemMeta(meta);
			head.setAmount(serverData.getPlayerCount() > 0 ? serverData.getPlayerCount() : 1);

			getInventory().setItem((9 + slots.get(slotsIndex)), head);
			this.slots.put((9 + slots.get(slotsIndex)), serverIndex);
			slotsIndex++;
		}
	}

	@Override
	public void onClose(InventoryCloseEvent event) {

	}

	public static List<Integer> getSlots(int items) {
		switch(items) {
			case 1:
				return Collections.singletonList(4);
			case 2:
				return Arrays.asList(3, 5);
			case 3:
				return Arrays.asList(2, 4, 6);
			case 4:
				return Arrays.asList(1, 3, 5, 7);
			case 5:
				return Arrays.asList(0, 2, 4, 6, 8);
			case 6:
				return Arrays.asList(0, 2, 3, 5, 6, 8);
			case 7:
				return Arrays.asList(1, 2, 3, 4, 5, 6, 7);
			case 8:
				return Arrays.asList(0, 1, 2, 3, 5, 6, 7, 8);
			case 9:
				return Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8);
		}
		return null;
	}

	public ItemStack getCustomHead(String url) {

		ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
		SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
		Field field = null;

		assert skullMeta != null;

		if (url.length() < 16) {

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

		} catch (Exception e) {
			e.printStackTrace();
		}

		skull.setItemMeta(skullMeta);

		return skull; //Finally, you have the custom head!

	}
}
