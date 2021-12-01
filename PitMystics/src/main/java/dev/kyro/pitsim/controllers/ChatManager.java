package dev.kyro.pitsim.controllers;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.commands.RapeCommand;
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
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class ChatManager implements Listener {

	@EventHandler
	public void autoCorrect(AsyncPlayerChatEvent event) {

		for(Player recipient : event.getRecipients()) {
			PitPlayer recipientPlayer = PitPlayer.getPitPlayer(recipient);
			if(recipientPlayer.disabledPlayerChat) event.getRecipients().remove(recipient);
			if(event.getPlayer().equals(recipient) && recipientPlayer.disabledPlayerChat) AOutput.error(event.getPlayer(), "&cYou currently have the chat muted. To disable this, navigate to the Chat Options menu located in the &f/donator &cmenu.");
		}

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
			AOutput.send(event.getPlayer(), "&aSuccessfully renamed item!");
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

		RapeCommand.chatMap.putIfAbsent(event.getPlayer(), new ArrayList<>());
		RapeCommand.chatMap.get(event.getPlayer()).add(event.getMessage().toLowerCase());
	}

	@EventHandler
	public void onCommandSend(PlayerCommandPreprocessEvent event) {
//		if(ChatColor.stripColor(event.getMessage()).startsWith("/view")) {
//			String newCommand = "invsee";
//			String[] splited = event.getMessage().split("\\s+");
//			if(splited.length < 2) return;
//
//			newCommand += " " + splited[1];
//			if(splited.length >= 3) newCommand += " " + splited[2];
//
//			event.setCancelled(true);
//			event.getPlayer().performCommand(newCommand);
//		}
	}
}
