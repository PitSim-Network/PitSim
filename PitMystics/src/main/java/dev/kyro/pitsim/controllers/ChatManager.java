package dev.kyro.pitsim.controllers;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.inventories.ChatColorPanel;
import dev.kyro.pitsim.misc.ItemRename;
import dev.kyro.pitsim.misc.Misc;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ChatManager implements Listener {

	@EventHandler
	public void autoCorrect(AsyncPlayerChatEvent event) {

		if(ChatColorPanel.playerChatColors.containsKey(event.getPlayer()) && event.getPlayer().hasPermission("pitsim.chatcolor")) {
			event.setMessage(ChatColorPanel.playerChatColors.get(event.getPlayer()).chatColor + event.getMessage());
		}

		if(ItemRename.renamePlayers.containsKey(event.getPlayer())){
			event.setCancelled(true);
			ItemStack heldItem = ItemRename.renamePlayers.get(event.getPlayer());

			if(Misc.isAirOrNull(heldItem)) {
				ItemRename.renamePlayers.remove(event.getPlayer());
				return;
			}
			NBTItem nbtItem = new NBTItem(heldItem);
			if(!nbtItem.hasKey(NBTTag.PIT_ENCHANT_ORDER.getRef()) || nbtItem.hasKey(NBTTag.JEWEL_KILLS.getRef())) {
				AOutput.error(event.getPlayer(), "&cYou can only name mystic items!");
				return;
			}
			ItemMeta meta = heldItem.getItemMeta();
			meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', event.getMessage()));
			heldItem.setItemMeta(meta);

			ItemRename.renamePlayers.remove(event.getPlayer());
		}


		String message = event.getMessage()
				.replaceAll("pitsandbox", "shitsandbox")
				.replaceAll("Pitsandbox", "Shitsandbox")
				.replaceAll("PitSandbox", "ShitSandbox")
				.replaceAll("pit sandbox", "shit sandbox")
				.replaceAll("Pit sandbox", "Shit sandbox")
				.replaceAll("Pit sandbox", "Shit sandbox")
				.replaceAll("Harry", "Hairy")
				.replaceAll("harry", "hairy")
				.replaceAll("(?i)pitsandbox", "shitsandbox")
				.replaceAll("(?i)pit sandbox", "shit sandbox")
				.replaceAll("(?i)harry", "hairy");

		event.setMessage(message);
	}
}
