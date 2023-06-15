package net.pitsim.pitsim.auction;

import com.sk89q.worldguard.bukkit.BukkitUtil;
import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import dev.kyro.arcticapi.misc.AOutput;
import net.pitsim.pitsim.PitSim;
import net.pitsim.pitsim.controllers.MapManager;
import net.pitsim.pitsim.controllers.objects.Mappable;
import net.pitsim.pitsim.controllers.objects.PluginMessage;
import net.pitsim.pitsim.enums.ItemType;
import net.pitsim.pitsim.events.MessageEvent;
import net.pitsim.pitsim.events.PitJoinEvent;
import net.pitsim.pitsim.misc.Formatter;
import net.pitsim.pitsim.misc.Misc;
import net.pitsim.pitsim.misc.Sounds;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class AuctionManager implements Listener {

	//Needs to be duplicated on both spigot and bungee end
	public static final int AUCTION_NUM = 3;

	public static AuctionItem[] auctionItems = new AuctionItem[AUCTION_NUM];

	public static Location spawnLoc = new Location(MapManager.getDarkzone(), 178.5, 52, -1004.5, 180, 0);
	public static Location returnLoc = new Location(MapManager.getDarkzone(), 244.5, 91, 8.5, 160, 0);

	public static long endTime = 0;

	public List<Player> animationPlayers = new ArrayList<>();

	public static int repetitions = 0;

	public static void onStart() {

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
				playTeleportAnimation(player, spawnLoc);
			}

			if(region.getId().equals("darkauctionexit")) {
				playTeleportAnimation(player, returnLoc);
			}
		}
	}

	@EventHandler
	public void onJoin(PitJoinEvent event) {
		Player player = event.getPlayer();

		new BukkitRunnable() {
			@Override
			public void run() {
				for(AuctionItem auctionItem : auctionItems) {
					if(auctionItem == null || auctionItem.bidMap == null) continue;
					if(!auctionItem.bidMap.containsKey(player.getUniqueId())) continue;

					UUID highestBidder = auctionItem.getHighestBidder();
					if(highestBidder == null) continue;

					String bidderName = Misc.getRankColor(highestBidder) + auctionItem.nameMap.get(highestBidder);
					int bid = auctionItem.bidMap.get(highestBidder);
					String itemName = auctionItem.item.itemName;

					if(player.getUniqueId().equals(highestBidder)) {
						AOutput.send(player, "&5&lDARK AUCTION! &7You are holding the top bid of &f" + bid + " Souls &7for " + itemName + "&7!");
					} else {
						AOutput.send(player, "&5&lDARK AUCTION! &7" + bidderName + " &7is holding the top bid of &f" + bid + " Souls &7for " + itemName + "&7!");
					}
					Sounds.BOOSTER_REMIND.play(player);
				}
			}
		}.runTaskLater(PitSim.INSTANCE, 20);

	}

	private void playTeleportAnimation(Player player, Location returnLoc) {
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

	@EventHandler
	public static void onMessageReceived(MessageEvent event) {
		PluginMessage message = event.getMessage();
		List<String> strings = message.getStrings();
		List<Integer> ints = message.getIntegers();
		List<Boolean> booleans = message.getBooleans();
		List<Long> longs = message.getLongs();

		if(strings.size() > 0 && strings.get(0).equals("AUCTION BID RESPONSE")) {
			boolean response = booleans.get(0);
			String player = strings.get(1);
			AsyncBidTask.getTask(UUID.fromString(player)).respond(response);
			return;
		}

		if(strings.size() > 0 && strings.get(0).equals("AUCTION BID DATA")) {
			String bidMapString = strings.get(1);
			String nameData = strings.get(2);
			int slot = ints.get(0);
			auctionItems[slot].bidMap = getBidData(bidMapString);
			auctionItems[slot].nameMap = getNameData(nameData);
			if(PitSim.status.isDarkzone()) AuctionDisplays.updateHolograms();

			for(Player player : Bukkit.getOnlinePlayers()) {
				if(!auctionItems[slot].bidMap.containsKey(player.getUniqueId())) continue;
				UUID highestBidder = auctionItems[slot].getHighestBidder();
				if(highestBidder == null) continue;
				if(player.getUniqueId().equals(highestBidder)) continue;

				String bidderName = Misc.getRankColor(highestBidder) + auctionItems[slot].nameMap.get(highestBidder);
				int bid = auctionItems[slot].bidMap.get(highestBidder);
				String itemName = auctionItems[slot].item.itemName;

				AOutput.send(player, "&5&lDARK AUCTION! &7" + bidderName + " &7has bid &f" + bid + " Souls &7for " + itemName + "&7!");
				Sounds.BOOSTER_REMIND.play(player);
			}

			return;
		}

		if(strings.size() < 1 || !strings.get(0).equals("AUCTION DATA")) return;
		long itemSeed = longs.get(0);
		long dataSeed = longs.get(1);

		ItemType itemType = ItemType.getItem(itemSeed);
		if(itemType == null) throw new RuntimeException("Item type is null! " + itemSeed + " " + dataSeed);

		int itemData = ItemType.getJewelData(itemType.item, dataSeed);
		int slot = ints.get(0);

		endTime = longs.get(2);

		String bidMapString = strings.get(1);
		String nameData = strings.get(2);

		if(auctionItems[slot] != null) auctionItems[slot].endAuction();

		auctionItems[slot] = new AuctionItem(itemType, itemData, slot, getBidData(bidMapString), getNameData(nameData));

		repetitions++;
		if(repetitions < AUCTION_NUM) return;
		repetitions = 0;

		new BukkitRunnable() {
			@Override
			public void run() {
				if(!PitSim.status.isDarkzone()) return;
				AuctionDisplays.showItems();
			}
		}.runTask(PitSim.INSTANCE);
	}

	public static Map<UUID, Integer> getBidData(String bidData) {
		Map<UUID, Integer> bidMap = new LinkedHashMap<>();
		for(String s : bidData.split(",")) {
			if(s.isEmpty()) continue;

			String[] split = s.split(":");
			bidMap.put(UUID.fromString(split[0]), Integer.parseInt(split[1]));
		}
		return bidMap;
	}

	public static Map<UUID, String> getNameData(String bidData) {
		Map<UUID, String> nameMap = new LinkedHashMap<>();
		for(String s : bidData.split(",")) {
			if(s.isEmpty()) continue;

			String[] split = s.split(":");
			nameMap.put(UUID.fromString(split[0]), split[1]);
		}
		return nameMap;
	}

	public static boolean haveAuctionsEnded() {
		return System.currentTimeMillis() > endTime;
	}

	public static String getRemainingTime() {
		return Formatter.formatDurationFull(endTime - System.currentTimeMillis(), true);
	}

	public static ChatTriggerAuctionItem[] getChatTriggerAuctionItems() {
		ChatTriggerAuctionItem[] triggerItems = new ChatTriggerAuctionItem[AUCTION_NUM];

		for(int i = 0; i < AUCTION_NUM; i++) {
			AuctionItem auctionItem = auctionItems[i];
			String name = auctionItem.nameMap.get(auctionItem.getHighestBidder());
			triggerItems[i] = new ChatTriggerAuctionItem(auctionItem.item.itemName, name == null ? "None" : name,
					auctionItem.getHighestBid());

		}

		return triggerItems;
	}

	public static class ChatTriggerAuctionItem implements Mappable {
		public String itemName;
		public String topBidder;
		public int topBid;

		public ChatTriggerAuctionItem(String itemName, String topBidder, int topBid) {
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
