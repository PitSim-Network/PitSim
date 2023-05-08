package dev.kyro.pitsim.controllers;

import com.sk89q.worldguard.bukkit.BukkitUtil;
import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.AuctionItem;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.controllers.objects.PluginMessage;
import dev.kyro.pitsim.enums.ItemType;
import dev.kyro.pitsim.events.MessageEvent;
import dev.kyro.pitsim.misc.Formatter;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ExecutionException;

public class AuctionManager implements Listener {
	public static final int AUCTION_NUM = 3;
	public static AuctionItem[] auctionItems = new AuctionItem[AUCTION_NUM];

	public static Location spawnLoc = new Location(MapManager.getDarkzone(), 178.5, 52, -1004.5, 180, 0);
	public static Location returnLoc = new Location(MapManager.getDarkzone(), 244.5, 91, 8.5, 160, 0);

	public List<Player> animationPlayers = new ArrayList<>();

	public AuctionManager() {
		new BukkitRunnable() {
			@Override
			public void run() {
//				TODO: FIX CODE IN OTHER TOOD

				if(haveAuctionsEnded()) {
					for(AuctionItem item : auctionItems) item.endAuction();
					generateNewAuctions();
					AuctionDisplays.showItems();
					CrossServerMessageManager.updateAllServers();
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 10, 10);
		new BukkitRunnable() {
			@Override
			public void run() {
				CrossServerMessageManager.updateAllServers();
			}
		}.runTaskLater(PitSim.INSTANCE, 20 * 20);
	}

	public static void onStart() {
		for(int i = 0; i < 3; i++) {
			if(FirestoreManager.AUCTION.auctions.get(i) == null) continue;

			int item = FirestoreManager.AUCTION.auctions.get(i).item;
			int itemData = FirestoreManager.AUCTION.auctions.get(i).itemData;

			List<String> bids = FirestoreManager.AUCTION.auctions.get(i).bids;
			Map<UUID, Integer> bidMap = new LinkedHashMap<>();
			for(String bid : bids) {
				String[] split = bid.split(":");
				bidMap.put(UUID.fromString(split[0]), Integer.parseInt(split[1]));
			}

			auctionItems[i] = new AuctionItem(ItemType.getItemType(item), itemData, i, bidMap);
//			sendAlert("Loaded auction int slot " + i + " with " + auctionItems[i].item.itemName);
		}

		if(auctionItems[0] == null) generateNewAuctions();

		AuctionDisplays.showItems();
	}

	public static ItemType generateItem() {
		double random = Math.random() * 100;

		List<ItemType> itemTypes = Arrays.asList(ItemType.values());
		Collections.shuffle(itemTypes);

		for(ItemType itemType : itemTypes) if(itemType.chance > random) return itemType;

		return itemTypes.get(0);
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {

		Player player = event.getPlayer();
		if(animationPlayers.contains(player)) return;
		RegionContainer container = WorldGuardPlugin.inst().getRegionContainer();
		RegionManager regions = container.get(event.getTo().getWorld());
		assert regions != null;
		ApplicableRegionSet set = regions.getApplicableRegions((BukkitUtil.toVector(event.getTo())));

		for(ProtectedRegion region : set) {
			if(region.getId().equals("darkauctionenterance")) {

				animationPlayers.add(player);
				Misc.applyPotionEffect(player, PotionEffectType.BLINDNESS, 60, 99, false, false);
				Sounds.MANA.play(player);

				new BukkitRunnable() {
					@Override
					public void run() {
						player.teleport(spawnLoc);
						animationPlayers.remove(player);
					}
				}.runTaskLater(PitSim.INSTANCE, 20);
			}

			if(region.getId().equals("darkauctionexit")) {

				animationPlayers.add(player);
				Misc.applyPotionEffect(player, PotionEffectType.BLINDNESS, 60, 99, false, false);
				Sounds.MANA.play(player);

				new BukkitRunnable() {
					@Override
					public void run() {
						player.teleport(returnLoc);
						animationPlayers.remove(player);
					}
				}.runTaskLater(PitSim.INSTANCE, 20);
			}
		}
	}

	@EventHandler
	public static void onMessageReceived(MessageEvent event) throws ExecutionException, InterruptedException {

		PluginMessage initialMessage = null;

		for(PluginMessage waitingMessage : AuctionItem.waitingMessages) {
			if(event.getMessage().isResponseTo(waitingMessage)) initialMessage = waitingMessage;
		}

		if(initialMessage == null) return;

		boolean response = event.getMessage().getBooleans().get(0);
		if(!response) {
			PitPlayer pitPlayer = FirestoreManager.FIRESTORE.collection(FirestoreManager.PLAYERDATA_COLLECTION)
					.document(initialMessage.getStrings().get(4)).get().get().toObject(PitPlayer.class);

			assert pitPlayer != null;
			List<Integer> ints = initialMessage.getIntegers();
			pitPlayer.auctionReturn.add(ints.get(0) + ":" + ints.get(1) + ":" + ints.get(2));

			FirestoreManager.FIRESTORE.collection(FirestoreManager.PLAYERDATA_COLLECTION).document(initialMessage.getStrings().get(4)).set(pitPlayer);
		}

		PluginMessage finalInitialMessage = initialMessage;
		new BukkitRunnable() {
			@Override
			public void run() {
				AuctionItem.waitingMessages.remove(finalInitialMessage);
			}
		}.runTaskLater(PitSim.INSTANCE, 1);
	}

	public static void generateNewAuctions() {
		FirestoreManager.AUCTION.endTime = System.currentTimeMillis() + (new Random().nextInt(60 * 12) + 60 * 6) * 60 * 1000;
//		FirestoreManager.AUCTION.endTime = System.currentTimeMillis() + 10 * 1000;

		for(int i = 0; i < auctionItems.length; i++) {
			auctionItems[i] = new AuctionItem(generateItem(), 0, i, null);
			sendAlert("Created new auction int slot " + i + " with " + auctionItems[i].item.itemName);
		}

		FirestoreManager.AUCTION.save();
	}

	public static boolean haveAuctionsEnded() {
		return haveAuctionsEnded(getAuctionEndTime());
	}

	public static boolean haveAuctionsEnded(long endTime) {
		return System.currentTimeMillis() > endTime;
	}

	public static String getRemainingTime() {
		return getRemainingTime(getAuctionEndTime());
	}

	public static String getRemainingTime(long endTime) {
		return Formatter.formatDurationFull(endTime - System.currentTimeMillis(), true);
	}

	public static long getAuctionEndTime() {
		return FirestoreManager.AUCTION.endTime;
	}

	public static void sendAlert(String message) {
		if(FirestoreManager.Collection.getCollection(PitSim.serverName) != FirestoreManager.Collection.PITSIM) return;

		PluginMessage pluginMessage = new PluginMessage().writeString("AUCTION ALERT").writeString(message + " " + System.currentTimeMillis());
		pluginMessage.send();
	}
}
