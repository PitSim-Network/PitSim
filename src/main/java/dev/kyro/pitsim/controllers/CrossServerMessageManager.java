package dev.kyro.pitsim.controllers;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.commands.ShowCommand;
import dev.kyro.pitsim.controllers.objects.AuctionItem;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.controllers.objects.PluginMessage;
import dev.kyro.pitsim.events.MessageEvent;
import dev.kyro.pitsim.megastreaks.Uberstreak;
import dev.kyro.pitsim.misc.CustomSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class CrossServerMessageManager implements Listener {
	public static String[] auctionNames = new String[AuctionManager.AUCTION_NUM];
	public static long auctionEndTime;

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				loadAuctionData();
			}
		}.runTaskLater(PitSim.INSTANCE, 20);
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
			sendAuctionData(strings.get(1));
		} else if(strings.get(0).equals("AUCTIONDATA")) {
//			overworld receiving data from darkzone
			if(PitSim.status.isDarkzone()) throw new RuntimeException();
			auctionEndTime = longs.get(0);
			strings.remove(0);
			for(int i = 0; i < auctionNames.length; i++) auctionNames[i] = strings.get(i);
		}
	}

//	Called by darkzone to send data to overworld server(s)
	public static void sendAuctionData(String serverName) {
		if(PitSim.status.isPitsim()) throw new RuntimeException();
		PluginMessage pluginMessage = new PluginMessage()
				.writeString("AUCTIONDATA")
				.writeString(serverName)
				.writeLong(AuctionManager.getAuctionEndTime());
		for(AuctionItem auctionItem : AuctionManager.auctionItems) {
			if(auctionItem == null) {
				pluginMessage.writeString(null);
				continue;
			}
			pluginMessage.writeString(auctionItem.item.itemName);
		}
		pluginMessage.send();
		AOutput.log("Sending auction data to overworld servers");
	}

//	Called by overworld or darkzone if they don't have data loaded
	public static void loadAuctionData() {
		if(!AuctionManager.haveAuctionsEnded(auctionEndTime)) return;
		if(PitSim.status.isDarkzone()) {
			for(int i = 0; i < auctionNames.length; i++) {
				auctionEndTime = AuctionManager.getAuctionEndTime();
				AuctionItem auctionItem = AuctionManager.auctionItems[i];
				if(auctionItem == null) {
					auctionNames[i] = null;
					continue;
				}
				auctionNames[i] = auctionItem.item.itemName;
			}
		} else {
			new PluginMessage()
					.writeString("AUCTIONREQUEST")
					.writeString(PitSim.serverName)
					.send();
			AOutput.log("Requesting auction data from darkzone");
		}
		for(Player player : ChatTriggerManager.getSubscribedPlayers()) ChatTriggerManager.sendAuctionInfo(PitPlayer.getPitPlayer(player));
	}
}
