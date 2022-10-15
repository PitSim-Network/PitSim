package dev.kyro.pitsim.controllers;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.inventories.ChatColorPanel;
import dev.kyro.pitsim.misc.ItemRename;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ChatManager implements Listener {

	@EventHandler
	public void autoCorrect(AsyncPlayerChatEvent event) {
		String message = event.getMessage();

		for(Player recipient : event.getRecipients()) {
			PitPlayer recipientPlayer = PitPlayer.getPitPlayer(recipient);
			if(recipientPlayer.playerChatDisabled) event.getRecipients().remove(recipient);
			if(event.getPlayer().equals(recipient) && recipientPlayer.playerChatDisabled)
				AOutput.error(event.getPlayer(), "&cYou currently have the chat muted. To disable this," +
						"navigate to the Chat Options menu located in the &f/donator &cmenu.");
		}

		if(ItemRename.renamePlayers.containsKey(event.getPlayer())) {
			ItemStack heldItem = ItemRename.renamePlayers.get(event.getPlayer());
			event.setCancelled(true);

			if(Misc.isAirOrNull(heldItem)) {
				ItemRename.renamePlayers.remove(event.getPlayer());
				return;
			}
			NBTItem nbtItem = new NBTItem(heldItem);
			if(!nbtItem.hasKey(NBTTag.ITEM_UUID.getRef())) {
				AOutput.error(event.getPlayer(), "&cYou can only name mystic items!");
				return;
			}
			ItemMeta meta = heldItem.getItemMeta();
			meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', message));
			heldItem.setItemMeta(meta);
			AOutput.send(event.getPlayer(), "&aSuccessfully renamed item!");
			ItemRename.renamePlayers.remove(event.getPlayer());
		}

		message = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', message));
		if(ChatColorPanel.playerChatColors.containsKey(event.getPlayer()) && event.getPlayer().hasPermission("pitsim.chatcolor")) {
			event.setMessage(ChatColorPanel.playerChatColors.get(event.getPlayer()).chatColor + message);
		}

		message = message.replaceAll("pitsandbox", "shitsandbox")
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
