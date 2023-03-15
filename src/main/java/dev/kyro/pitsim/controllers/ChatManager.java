package dev.kyro.pitsim.controllers;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.ahelp.HelpManager;
import dev.kyro.pitsim.aitems.PitItem;
import dev.kyro.pitsim.commands.essentials.GamemodeCommand;
import dev.kyro.pitsim.commands.essentials.TeleportCommand;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.inventories.ChatColorPanel;
import dev.kyro.pitsim.misc.ItemRename;
import dev.kyro.pitsim.misc.Misc;
import me.clip.deluxechat.events.PrivateMessageEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class ChatManager implements Listener {
	public static List<String> illegalPhrases = new ArrayList<>();
	public static Map<Player, Integer> helpCooldownMap = new HashMap<>();

	static {
		illegalPhrases.add("kyro");
		illegalPhrases.add("wiji");
	}

	@EventHandler
	public void onPrivateMessage(PrivateMessageEvent event) {
		Player sender = event.getSender();
		Player recipient = event.getRecipient();
		PitPlayer pitSender = PitPlayer.getPitPlayer(sender);
		PitPlayer pitRecipient = PitPlayer.getPitPlayer(recipient);

		if(pitRecipient.uuidIgnoreList.contains(sender.getUniqueId().toString())) {
			event.setCancelled(true);
			AOutput.error(sender, "&c&lERROR!&7 That player has you ignored");
		} else if(pitSender.uuidIgnoreList.contains(recipient.getUniqueId().toString())) {
			event.setCancelled(true);
			AOutput.error(sender, "&c&lERROR!&7 You have that player ignored");
		}
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		String message = event.getMessage();

		if(message.contains("I joined using ChatCraft")) {
			AOutput.send(player, "&c&lCOPE!&7 We literally don't care! :)");
			event.setCancelled(true);
			return;
		}

		for(Player recipient : new ArrayList<>(event.getRecipients())) {
			PitPlayer recipientPitPlayer = PitPlayer.getPitPlayer(recipient);

			if(player == recipient && recipientPitPlayer.playerChatDisabled) {
				AOutput.error(player, "&cYou currently have the chat muted. To disable this," +
						"navigate to the Chat Options menu located in the &f/donator &cmenu.");
				event.setCancelled(true);
				return;
			}
			if(recipientPitPlayer.playerChatDisabled || recipientPitPlayer.uuidIgnoreList.contains(player.getUniqueId().toString()))
				event.getRecipients().remove(recipient);
		}

		if(ItemRename.renamePlayers.containsKey(player)) {
			String name = ChatColor.translateAlternateColorCodes('&', message);
			String strippedName = ChatColor.stripColor(name);
			ItemStack heldItem = ItemRename.renamePlayers.get(player);
			event.setCancelled(true);

			if(Misc.isAirOrNull(heldItem)) {
				ItemRename.renamePlayers.remove(player);
				return;
			}
			PitItem pitItem = ItemFactory.getItem(heldItem);
			if(pitItem == null || !pitItem.isMystic) {
				AOutput.error(player, "&cYou can only name mystic items!");
				return;
			}
			if(!strippedName.matches("[\\w\\s]+")) {
				AOutput.error(player, "&c&lERROR!&7 You can only use regular characters");
				return;
			}
			if(!player.isOp()) {
				for(String illegalPhrase : illegalPhrases) {
					if(!strippedName.toLowerCase().contains(illegalPhrase)) continue;
					AOutput.error(player, "&c&lERROR!&7 Name contains illegal phrase \"" + illegalPhrase + "\"");
					return;
				}
			}
			if(!player.isOp() && strippedName.length() > 32) {
				AOutput.error(player, "&c&lERROR!&7 Item names cannot be longer than 32 characters");
				return;
			}
			ItemMeta meta = heldItem.getItemMeta();
			meta.setDisplayName(name);
			heldItem.setItemMeta(meta);
			AOutput.send(player, "&aSuccessfully renamed item!");
			ItemRename.renamePlayers.remove(player);
		}

		if(!player.isOp()) message = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', message));
		if(ChatColorPanel.playerChatColors.containsKey(player) && player.hasPermission("pitsim.chatcolor")) {
			message = ChatColorPanel.playerChatColors.get(player).chatColor + message;
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

		handleQuestion(player, message);
	}

	public static void handleQuestion(Player player, String message) {
		message = ChatColor.stripColor(message);
		if(message.length() > 250) return;

		HelpManager.HelperAgent helperAgent = HelpManager.getAgent(player);
		final boolean shouldStoreRequest;
		if(message.endsWith("?") && !helperAgent.isWaitingForResponse()) {
			HelpManager.StoredRequest storedRequest = HelpManager.getStoredRequest(message);
			if(storedRequest != null) {
				new BukkitRunnable() {
					@Override
					public void run() {
						helperAgent.executeIntent(storedRequest.getIntent());
					}
				}.runTaskLater(PitSim.INSTANCE, 1L);
				return;
			} else {
				shouldStoreRequest = true;
			}
		} else {
			shouldStoreRequest = false;
		}

		if(message.endsWith("?") && !player.isOp()) {
			int recentMessages = helpCooldownMap.getOrDefault(player, 0);
			if(recentMessages > 3) {
				sendMessageDelayed(player, "&9&lAI!&7 Please slow down with your requests");
				return;
			}
			helpCooldownMap.put(player, ++recentMessages);
			new BukkitRunnable() {
				@Override
				public void run() {
					int recentMessages = helpCooldownMap.getOrDefault(player, 0);
					if(recentMessages <= 1) {
						helpCooldownMap.remove(player);
					} else {
						helpCooldownMap.put(player, --recentMessages);
					}
				}
			}.runTaskLater(PitSim.INSTANCE, 20 * 60);
		}

		String finalMessage = message;
		new Thread(() -> {
			if(finalMessage.endsWith("?") || helperAgent.isWaitingForResponse()) {
				String intent = helperAgent.detectIntent(finalMessage);
				helperAgent.executeIntent(intent);
				if(shouldStoreRequest) {
					HelpManager.StoredRequest storedRequest = new HelpManager.StoredRequest(finalMessage, intent);
					HelpManager.writeStoredRequest(storedRequest);
					System.out.println("writing stored request");
				}
			}
		}).start();
	}

	public static void sendMessageDelayed(Player player, String message) {
		new BukkitRunnable() {
			@Override
			public void run() {
				AOutput.error(player, message);
			}
		}.runTaskLater(PitSim.INSTANCE, 1L);
	}

	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		String message = event.getMessage().toLowerCase();
		List<String> stringArgs = new ArrayList<>(Arrays.asList(message.split(" ")));
		String command = stringArgs.remove(0);
		String[] args = stringArgs.toArray(new String[0]);

		if(command.equalsIgnoreCase("/gamemode")) {
			event.setCancelled(true);
			GamemodeCommand.INSTANCE.onCommand(player, null, "gamemode", args);
		} else if(command.equalsIgnoreCase("/tp")) {
			event.setCancelled(true);
			TeleportCommand.INSTANCE.onCommand(player, null, "tp", args);
		}
	}
}
