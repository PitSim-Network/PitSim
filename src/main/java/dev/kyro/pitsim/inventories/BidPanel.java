package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.AuctionDisplays;
import dev.kyro.pitsim.controllers.AuctionManager;
import dev.kyro.pitsim.controllers.objects.AuctionItem;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.ItemType;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BidPanel extends AGUIPanel {
	PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
	public BidGUI bidGUI;
	public int slot;
	public BukkitTask runnable;

	public BidPanel(AGUI gui, int slot) {
		super(gui);
		bidGUI = (BidGUI) gui;
		inventoryBuilder.createBorder(Material.STAINED_GLASS_PANE, 8);
		this.slot = slot;
	}

	@Override
	public String getName() {
		return "Place a Bid";
	}

	@Override
	public int getRows() {
		return 3;
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		int slot = event.getSlot();
		if(event.getClickedInventory().getHolder() == this) {
			if(slot != 16) return;

			AuctionItem auctionItem = AuctionManager.auctionItems[this.slot];

			if(auctionItem.getHighestBidder() != null && auctionItem.getHighestBidder().equals(player.getUniqueId())) {
				Sounds.NO.play(player);
				AOutput.error(player, "&c&lNOPE! &7You are already the highest bidder!");
			} else if(pitPlayer.taintedSouls < minBid(auctionItem) - auctionItem.getBid(player.getUniqueId())) {
				Sounds.NO.play(player);
				AOutput.error(player, "&c&lNOPE! &7Not enough Souls!");
			} else {
				Sounds.RENOWN_SHOP_PURCHASE.play(player);
				pitPlayer.taintedSouls -= minBid(auctionItem) - auctionItem.getBid(player.getUniqueId());

				if(minBid(auctionItem) > pitPlayer.stats.highestBid) pitPlayer.stats.highestBid = minBid(auctionItem);
				auctionItem.addBid(player.getUniqueId(), minBid(auctionItem));
				player.closeInventory();
			}

		}
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
		runnable = new BukkitRunnable() {
			@Override
			public void run() {
				AuctionItem auctionItem = AuctionManager.auctionItems[slot];

				ItemStack item = auctionItem.itemData == 0 ? auctionItem.item.item.clone() : ItemType.getJewelItem(auctionItem.item.id, auctionItem.itemData);

				AItemStackBuilder itemBuilder = new AItemStackBuilder(item);
				itemBuilder.setName(auctionItem.item.itemName);
				if(auctionItem.itemData == 0) itemBuilder.setLore(auctionItem.item.item.getItemMeta().getLore());

				getInventory().setItem(13, itemBuilder.getItemStack());

				AItemStackBuilder bidsBuilder = new AItemStackBuilder(Material.MAP);
				bidsBuilder.setName(ChatColor.YELLOW + "Current Bids");
				List<String> bidsLore = new ArrayList<>();
				if(auctionItem.bidMap.size() == 0) bidsLore.add(ChatColor.GRAY + "No Bids Yet!");
				else bidsLore.add("");
				for(Map.Entry<UUID, Integer> entry : auctionItem.bidMap.entrySet()) {
					bidsLore.add(ChatColor.GOLD + Bukkit.getOfflinePlayer(entry.getKey()).getName() + ChatColor.WHITE + " " + entry.getValue() + " Souls");
				}
				bidsBuilder.setLore(bidsLore);

				getInventory().setItem(10, bidsBuilder.getItemStack());


				AItemStackBuilder placeBidBuilder = new AItemStackBuilder(Material.INK_SACK, 1, 7);
				placeBidBuilder.setName(ChatColor.YELLOW + "Place a Bid");
				List<String> bidLore = new ArrayList<>();
				bidLore.add("");
				if(auctionItem.getHighestBidder() != null)
					bidLore.add(ChatColor.GRAY + "Highest Bid: " + ChatColor.WHITE + auctionItem.getHighestBid() + " Souls");
				else
					bidLore.add(ChatColor.GRAY + "Starting Bid: " + ChatColor.WHITE + auctionItem.getHighestBid() + " Souls");
				bidLore.add(ChatColor.GRAY + "Your Bid: " + ChatColor.WHITE + auctionItem.getBid(player.getUniqueId()) + " Souls");
				bidLore.add("");
				bidLore.add(ChatColor.GRAY + "Your Souls: " + ChatColor.WHITE + pitPlayer.taintedSouls);
				bidLore.add("");
				if((AuctionManager.minutes * 60000L - (System.currentTimeMillis() - AuctionManager.auctionItems[0].initTime)) / 1000 < 0) {
					bidLore.add(ChatColor.YELLOW + "Ending Soon");
				} else bidLore.add(ChatColor.YELLOW + AuctionDisplays.getRemainingTime() + " Remaining"); ;
				bidLore.add("");

				if(auctionItem.getHighestBidder() != null && auctionItem.getHighestBidder().equals(player.getUniqueId())) {
					bidLore.add(ChatColor.GREEN + "You already have the Highest Bid");
				} else if(pitPlayer.taintedSouls < minBid(auctionItem) - auctionItem.getBid(player.getUniqueId())) {
					bidLore.add(ChatColor.RED + "Not enough Souls!");
				} else {
					bidLore.add(ChatColor.YELLOW + "Click to Bid " + ChatColor.WHITE + (minBid(auctionItem)) + " Souls" + ChatColor.YELLOW + "!");
				}
				placeBidBuilder.setLore(bidLore);

				getInventory().setItem(16, placeBidBuilder.getItemStack());

			}
		}.runTaskTimer(PitSim.INSTANCE, 0, 10);
	}

	public static int minBid(AuctionItem item) {
		int currentBid = item.getHighestBid();

		if(item.getHighestBidder() == null) return currentBid;

		int bid = (int) Math.ceil(currentBid + (currentBid * 0.1));
		return Math.max(1, bid);
	}

	@Override
	public void onClose(InventoryCloseEvent event) {
		runnable.cancel();
	}


}
