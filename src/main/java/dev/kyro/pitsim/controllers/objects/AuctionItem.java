package dev.kyro.pitsim.controllers.objects;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.battlepass.quests.WinAuctionsQuest;
import dev.kyro.pitsim.controllers.AuctionDisplays;
import dev.kyro.pitsim.enums.ItemType;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class AuctionItem {
	public ItemType item;
	public int itemData;
	public int slot;
	public Map<UUID, Integer> bidMap;
	public Map<UUID, String> nameMap;

	public AuctionItem(ItemType item, int itemData, int slot, Map<UUID, Integer> bidMap, Map<UUID, String> nameMap) {
		this.item = item;
		this.itemData = itemData;
		this.slot = slot;

		this.bidMap = bidMap == null ? new LinkedHashMap<>() : bidMap;
		this.nameMap = nameMap == null ? new LinkedHashMap<>() : nameMap;

		if(PitSim.status.isDarkzone()) AuctionDisplays.updateHolograms();
	}

	public void addBid(Player player, int bid) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		int currentBid = getBid(player.getUniqueId());

		new AsyncBidTask(slot, player, bid, () -> {
			pitPlayer.taintedSouls -= (bid - currentBid);
			Sounds.RENOWN_SHOP_PURCHASE.play(player);
			if(bid > pitPlayer.stats.highestBid) pitPlayer.stats.highestBid = bid;
			WinAuctionsQuest.INSTANCE.winAuction(pitPlayer);
		}, AsyncBidTask.getDefaultFail(player));
	}

	public int getHighestBid() {
		int highest = 0;
		for(Map.Entry<UUID, Integer> entry : bidMap.entrySet()) {
			if(entry.getValue() > highest) {
				highest = entry.getValue();
			}
		}
		return highest == 0 ? item.startingBid : highest;
	}

	public UUID getHighestBidder() {
		int highest = 0;
		UUID player = null;
		for(Map.Entry<UUID, Integer> entry : bidMap.entrySet()) {
			if(entry.getValue() > highest) {
				highest = entry.getValue();
				player = entry.getKey();
			}
		}
		return player;
	}

	public int getBid(UUID player) {
		return bidMap.getOrDefault(player, 0);
	}

	public void endAuction() {
		if(!PitSim.status.isDarkzone()) return;

		if(AuctionDisplays.pedestalArmorStands[slot] == null || AuctionDisplays.pedestalItems[slot] == null)
			throw new RuntimeException("Pedestal or item is null!");

		AuctionDisplays.getItem(AuctionDisplays.pedestalItems[slot]).remove();

		for(Entity nearbyEntity : AuctionDisplays.pedestalLocations[slot].getWorld().getNearbyEntities(AuctionDisplays.pedestalLocations[slot], 1, 1, 1)) {
			if(nearbyEntity instanceof ArmorStand) nearbyEntity.remove();
		}
	}
}
