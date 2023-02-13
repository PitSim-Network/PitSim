package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.AuctionManager;
import dev.kyro.pitsim.controllers.objects.AuctionItem;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.ItemType;
import dev.kyro.pitsim.misc.Sounds;
import dev.kyro.pitsim.misc.packets.SignPrompt;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

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
				AOutput.error(player, "&c&lERROR!&7 You are already the highest bidder!");
			} else if(pitPlayer.taintedSouls < minBid(auctionItem) - auctionItem.getBid(player.getUniqueId())) {
				Sounds.NO.play(player);
				AOutput.error(player, "&c&lERROR!&7 Not enough Souls!");
			} else {
				if(event.getClick() == ClickType.RIGHT || event.getClick() == ClickType.SHIFT_RIGHT) {
					SignPrompt.promptPlayer(player, "", "^^^^^^", "Enter Bid", "Min: " + minBid(auctionItem), input -> {
						int bid;
						try {
							bid = Integer.parseInt(input.replaceAll("\"", ""));
							if(bid < minBid(auctionItem)) throw new Exception();
						} catch(Exception ignored) {
							AOutput.error(player, "&c&lERROR!&7 Invalid bid");
							return;
						}

						if(pitPlayer.taintedSouls < bid - auctionItem.getBid(player.getUniqueId())) {
							Sounds.NO.play(player);
							AOutput.error(player, "&c&lERROR!&7 Not enough Souls!");
							return;
						}

						bid(auctionItem, pitPlayer, bid);
					});
				} else {
					bid(auctionItem, pitPlayer, minBid(auctionItem));
				}
			}
		}
	}

	public void bid(AuctionItem auctionItem, PitPlayer pitPlayer, int bid) {
		Sounds.RENOWN_SHOP_PURCHASE.play(player);
		pitPlayer.taintedSouls -= bid - auctionItem.getBid(player.getUniqueId());

		if(minBid(auctionItem) > pitPlayer.stats.highestBid) pitPlayer.stats.highestBid = minBid(auctionItem);
		auctionItem.addBid(player.getUniqueId(), bid);
		player.closeInventory();
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

				ALoreBuilder loreBuilder = new ALoreBuilder();
				if(auctionItem.bidMap.size() == 0) loreBuilder.addLore("&7No Bids Yet!"); else loreBuilder.addLore("");
				loreBuilder.addLore();

				for(Map.Entry<UUID, Integer> entry : auctionItem.bidMap.entrySet()) {
					loreBuilder.addLore("&6" + Bukkit.getOfflinePlayer(entry.getKey()).getName() + "&f " +
							entry.getValue() + " Souls");
				}

				AItemStackBuilder bidsBuilder = new AItemStackBuilder(Material.MAP)
						.setName("&eCurrent Bids")
						.setLore(loreBuilder);
				getInventory().setItem(10, bidsBuilder.getItemStack());

				loreBuilder = new ALoreBuilder("");
				if(auctionItem.getHighestBidder() != null)
					loreBuilder.addLore("&7Highest Bid: &f" + auctionItem.getHighestBid() + " Souls");
				else
					loreBuilder.addLore("&7Starting Bid: &f" + auctionItem.getHighestBid() + " Souls");
				loreBuilder.addLore(
						"&7Your Bid: &f" + auctionItem.getBid(player.getUniqueId()) + " Souls",
						"",
						"&7Your Souls: &f" + pitPlayer.taintedSouls,
						""
				);
				if(AuctionManager.haveAuctionsEnded()) {
					loreBuilder.addLore("&eEnding Soon");
				} else loreBuilder.addLore("&e" + AuctionManager.getRemainingTime() + " Remaining");
				loreBuilder.addLore("");
				if(auctionItem.getHighestBidder() != null && auctionItem.getHighestBidder().equals(player.getUniqueId())) {
					loreBuilder.addLore("&aYou already have the Highest Bid");
				} else if(pitPlayer.taintedSouls < minBid(auctionItem) - auctionItem.getBid(player.getUniqueId())) {
					loreBuilder.addLore("&cNot enough Souls!");
				} else {
					loreBuilder.addLore("&eLeft-Click to bid &f" + (minBid(auctionItem)) + " Souls" + "&e!");
					loreBuilder.addLore("&eRight-Click for a custom bid!");
				}
				AItemStackBuilder placeBidBuilder = new AItemStackBuilder(Material.GHAST_TEAR)
						.setName("&ePlace a Bid")
						.setLore(loreBuilder);

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
