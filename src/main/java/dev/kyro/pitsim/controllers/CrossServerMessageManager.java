package dev.kyro.pitsim.controllers;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.commands.ShowCommand;
import dev.kyro.pitsim.controllers.objects.AuctionItem;
import dev.kyro.pitsim.controllers.objects.Mappable;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.controllers.objects.PluginMessage;
import dev.kyro.pitsim.events.MessageEvent;
import dev.kyro.pitsim.megastreaks.Uberstreak;
import dev.kyro.pitsim.misc.CustomSerializer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CrossServerMessageManager implements Listener {
	public static CrossServerAuctionItem[] auctionItems = new CrossServerAuctionItem[AuctionManager.AUCTION_NUM];
	public static long auctionEndTime;

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				getOrRequestLocalAuctionData();
			}
		}.runTaskLater(PitSim.INSTANCE, 20 * 20);
	}

	@EventHandler
	public void onMessage(MessageEvent event) {
		PluginMessage message = event.getMessage();
		List<String> strings = message.getStrings();
		List<Integer> integers = message.getIntegers();
		List<Long> longs = message.getLongs();
		List<Boolean> booleans = message.getBooleans();
		if(strings.isEmpty()) return;

		if(strings.get(0).equals("ITEMSHOW")) {
			String displayName = strings.get(1);
			ItemStack itemStack = CustomSerializer.deserialize(strings.get(2));
			ShowCommand.sendShowMessage(displayName, itemStack);
		} else if(strings.get(0).equals("FINDJEWEL")) {
			String displayName = strings.get(1);
			ItemStack itemStack = CustomSerializer.deserialize(strings.get(2));
			EnchantManager.sendJewelFindMessage(displayName, itemStack);
		} else if(strings.get(0).equals("PRESTIGE")) {
			String displayName = strings.get(1);
			int prestige = integers.get(0);
			LevelManager.onPrestige(displayName, prestige);
		} else if(strings.get(0).equals("UBERDROP")) {
			String displayName = strings.get(1);
			ItemStack itemStack = CustomSerializer.deserialize(strings.get(2));
			Uberstreak.sendUberMessage(displayName, itemStack);
		} else if(strings.get(0).equals("AUCTIONREQUEST")) {
			AOutput.log("Received request for auction data");
			sendAuctionData(strings.get(1));
		} else if(strings.get(0).equals("AUCTIONDATA")) {
//			overworld receiving data from darkzone
			AOutput.log("Received auction data from darkzone");
			if(PitSim.status.isDarkzone()) throw new RuntimeException();
			auctionEndTime = longs.get(0);
			strings.remove(0);
			for(int i = 0; i < auctionItems.length; i++) auctionItems[i] =
					new CrossServerAuctionItem(strings.get(i * 2), strings.get(i * 2 + 1), integers.get(i));
			for(Player player : ChatTriggerManager.getSubscribedPlayers()) ChatTriggerManager.sendAuctionInfo(PitPlayer.getPitPlayer(player));
		}
	}

//	Called when darkzone auction data is updated
	public static void updateAllServers() {
		if(!PitSim.status.isDarkzone()) throw new RuntimeException();
		getOrRequestLocalAuctionData();
		sendAuctionData("");
	}

//	Called by darkzone to send data to an overworld server (or all of them)
	private static void sendAuctionData(String serverName) {
		if(!PitSim.status.isDarkzone()) throw new RuntimeException();
		PluginMessage pluginMessage = new PluginMessage()
				.writeString("AUCTIONDATA")
				.writeString(serverName)
				.writeLong(AuctionManager.getAuctionEndTime());
		for(AuctionItem auctionItem : AuctionManager.auctionItems) {
			if(auctionItem == null) {
				pluginMessage.writeString("None");
				pluginMessage.writeString("None");
				pluginMessage.writeInt(0);
				continue;
			}

			UUID highestBidderUUID = auctionItem.getHighestBidder();
			String highestBidderName = "None";
			if(highestBidderUUID != null) {
				OfflinePlayer highestBidder = Bukkit.getOfflinePlayer(auctionItem.getHighestBidder());
				highestBidderName = highestBidder.getName();
			}

			pluginMessage.writeString(auctionItem.item.itemName);
			pluginMessage.writeString(highestBidderName);
			pluginMessage.writeInt(auctionItem.getHighestBid());
		}
		pluginMessage.send();
		AOutput.log("Sending auction data to overworld servers");
	}

//	Called by overworld or darkzone if they don't have data loaded
	private static void getOrRequestLocalAuctionData() {
		if(PitSim.status.isDarkzone()) {
			for(int i = 0; i < auctionItems.length; i++) {
				auctionEndTime = AuctionManager.getAuctionEndTime();
				AuctionItem auctionItem = AuctionManager.auctionItems[i];
				if(auctionItem == null) {
					auctionItems[i] = null;
					continue;
				}

				auctionItems[i] = new CrossServerAuctionItem(auctionItem.item.itemName, getItemBidder(auctionItem), auctionItem.getHighestBid());
				for(Player player : ChatTriggerManager.getSubscribedPlayers()) ChatTriggerManager.sendAuctionInfo(PitPlayer.getPitPlayer(player));
			}
		} else {
			new PluginMessage()
					.writeString("AUCTIONREQUEST")
					.writeString(PitSim.serverName)
					.send();
			AOutput.log("Requesting auction data from darkzone");
		}
	}

	public static String getItemBidder(AuctionItem auctionItem) {
		UUID highestBidderUUID = auctionItem.getHighestBidder();
		String highestBidderName = "None";
		if(highestBidderUUID != null) {
			OfflinePlayer highestBidder = Bukkit.getOfflinePlayer(auctionItem.getHighestBidder());
			highestBidderName = highestBidder.getName();
		}
		return highestBidderName;
	}

	public static class CrossServerAuctionItem implements Mappable {
		public String itemName;
		public String topBidder;
		public int topBid;

		public CrossServerAuctionItem(String itemName, String topBidder, int topBid) {
			this.itemName = itemName;
			this.topBidder = topBidder;
			this.topBid = topBid;
		}

		@Override
		public Map<String, Object> getAsMap() {
			Map<String, Object> dataMap = new HashMap<>();
			dataMap.put("itemName", itemName);
			dataMap.put("topBidder", topBidder);
			dataMap.put("topBid", topBid);
			return dataMap;
		}
	}
}
