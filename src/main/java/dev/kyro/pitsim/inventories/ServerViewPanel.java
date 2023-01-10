package dev.kyro.pitsim.inventories;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.ProxyMessaging;
import dev.kyro.pitsim.controllers.objects.PluginMessage;
import dev.kyro.pitsim.controllers.objects.ServerData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ServerViewPanel extends AGUIPanel {

	public ServerData data;
	public Map<Integer, String> slots = new HashMap<>();
	int rows;

	public ServerViewPanel(AGUI gui, ServerData data) {
		super(gui, true);
		this.data = data;
		this.rows = (int) Math.min(Math.ceil(data.getPlayerCount() / 7.0), 3);

		buildInventory();
		inventoryBuilder.createBorder(Material.STAINED_GLASS_PANE, 7);

		int slot = 9;
		for(Map.Entry<String, String> entry : data.getPlayers().entrySet()) {
			while(slot % 9 == 0 || slot % 9 == 8) {
				slot++;
			}

			String name = entry.getKey();
			String playerString = entry.getValue();

			ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
			SkullMeta headMeta = (SkullMeta) head.getItemMeta();
			headMeta.setOwner(name);
			head.setItemMeta(headMeta);

			new AItemStackBuilder(head)
					.setName(playerString)
					.setLore(new ALoreBuilder().addLore(
							"",
							"&eLeft-Click to teleport",
							"&eRight-Click to edit"
							));

			getInventory().setItem(slot, head);
			slots.put(slot, name);
			slot++;

			ItemStack backItem = new AItemStackBuilder(Material.ARROW)
					.setName("&eBack")
					.setLore(new ALoreBuilder(
							"&7To Admin Menu"
					))
					.getItemStack();
			getInventory().setItem((getRows() * 9) - 5, backItem);
		}
	}

	@Override
	public String getName() {
		return (data.isDarkzone() ? "Darkzone-" :  "PitSim-") + (data.index + 1);
	}

	@Override
	public int getRows() {
		return rows + 2;
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		int slot = event.getSlot();
		if(event.getClickedInventory().getHolder() == this) {

			if(slot == (getRows() * 9) - 5) {
				openPreviousGUI();
			}

			if(!slots.containsKey(slot)) {
				return;
			}

			String name = slots.get(slot);

			if(event.isLeftClick()) {
				player.closeInventory();

				if(name.equalsIgnoreCase(player.getName())) {
					AOutput.error(player, "&cYou cannot teleport to yourself!");
					Sounds.NO.play(player);
					return;
				}

				Player tpPlayer = Bukkit.getPlayer(name);
				if(tpPlayer != null && tpPlayer.isOnline()) {
					player.teleport(tpPlayer);
					AOutput.send(player, "&aTeleporting you to " + name);
					return;
				}

				for(String s : new ArrayList<>(data.getPlayers().keySet())) {
					if(s.equalsIgnoreCase(player.getName())) {
						AOutput.error(player, "&cYou are already in this server!");
						Sounds.NO.play(player);
						return;
					}
				}

				PluginMessage teleport = new PluginMessage().writeString("TELEPORT JOIN");
				teleport.writeString(player.getUniqueId().toString());
				teleport.writeString(name);
				teleport.writeBoolean(data.isDarkzone()).writeInt(data.index).send();

				if(data.isDarkzone()) ProxyMessaging.darkzoneSwitchPlayer(player, data.index + 1);
				else ProxyMessaging.switchPlayer(player, data.index + 1);
			}

			if(event.isRightClick()) {
				PluginMessage edit = new PluginMessage().writeString("EDIT PLAYER");
				edit.writeString(player.getUniqueId().toString());
				edit.writeString(name).send();
				player.closeInventory();
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
