package dev.kyro.pitsim.market;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.misc.Sounds;
import dev.kyro.pitsim.misc.packets.SignPrompt;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ListingInspectPanel extends AGUIPanel {
	public MarketListing listing;
	public int bid;
	public int purchasing;
	public BukkitTask runnable;
	int soulsToTake;
	public boolean marketPanel;

	public ListingInspectPanel(AGUI gui, MarketListing listing, boolean marketPanel) {
		super(gui);
		this.listing = listing;
		bid = listing.getMinimumBid();
		purchasing = 1;
		inventoryBuilder.createBorder(Material.STAINED_GLASS_PANE, 15);
		this.marketPanel = marketPanel;

		calculateItems();
	}

	public void calculateItems() {
		ItemStack listingStack = listing.getItemStack();
		ItemMeta listingMeta = listingStack.getItemMeta();
		List<String> listingLore = listingMeta.getLore();
		listingLore.subList(listingLore.size() - 2, listingLore.size()).clear();
		listingMeta.setLore(listingLore);
		listingStack.setItemMeta(listingMeta);
		getInventory().setItem(13, listingStack);

		AItemStackBuilder auctionBuilder = new AItemStackBuilder(listing.startingBid != -1 ? Material.GOLD_BARDING : Material.BARRIER)
				.setName(listing.startingBid != -1 ? "&ePlace Bid" : "&cPlace Bid");

		ALoreBuilder loreBuilder = new ALoreBuilder();
		if(listing.startingBid != -1) {
			soulsToTake = bid - listing.bidMap.getOrDefault(player.getUniqueId(), 0);

			loreBuilder.addLore((listing.bidMap.isEmpty() ? "&7Starting Bid: &f" : "&7Minimum Bid: &f") + listing.getMinimumBid() + " Souls");
			loreBuilder.addLore("");
			if(listing.bidMap.containsKey(player.getUniqueId()))
				loreBuilder.addLore("&7Your Bid: &f" + listing.bidMap.get(player.getUniqueId()) + " Souls");
			else loreBuilder.addLore("&7Your Bid: &cNone");
			loreBuilder.addLore("&7Your Souls: &f" + PitPlayer.getPitPlayer(player).taintedSouls + " Souls");
			loreBuilder.addLore("");
			if(listing.ownerUUID.equals(player.getUniqueId())) {
				loreBuilder.addLore("&cYou cannot bid on your own listing!");
			} else if(listing.getHighestBidder() != null && listing.getHighestBidder().equals(player.getUniqueId())) {
				loreBuilder.addLore("&aYou are the highest bidder!");
			} else if(PitPlayer.getPitPlayer(player).taintedSouls < soulsToTake) {
				loreBuilder.addLore("&cNot enough souls!");
			} else {
				loreBuilder.addLore("&eLeft-Click to bid &f" + bid + " Souls");
				loreBuilder.addLore("&eRight-Click for custom amount");
			}
		} else {
			loreBuilder.addLore("&7This item is not up for auction!");
		}

		auctionBuilder.setLore(loreBuilder);
		getInventory().setItem(11, auctionBuilder.getItemStack());



		AItemStackBuilder binBuilder = new AItemStackBuilder(listing.binPrice != -1 ? Material.NAME_TAG : Material.BARRIER)
				.setName(listing.binPrice != -1 ? "&eBuy Item Now!" : "&cBuy Item Now!");

		ALoreBuilder binLoreBuilder = new ALoreBuilder();
		if(listing.binPrice != -1) {

			binLoreBuilder.addLore("&7BIN Price: &f" + listing.binPrice + " Souls" + (listing.stackBIN ? " &8(Per Item)" : ""));
			binLoreBuilder.addLore("&7Your Souls: &f" + PitPlayer.getPitPlayer(player).taintedSouls + " Souls");
			binLoreBuilder.addLore("");
			if(listing.stackBIN) {
				binLoreBuilder.addLore("&7Stock: &a" + listing.itemData.getAmount() + (listing.itemData.getAmount() == 1 ? " Item" : " Items"));
				binLoreBuilder.addLore("&7Purchasing: &e" + purchasing + (purchasing == 1 ? " Item" : " Items"));
				binLoreBuilder.addLore("&7Total Cost: &f" + (listing.binPrice * purchasing) + " Souls");
				binLoreBuilder.addLore("");
			}

			int cost = listing.stackBIN ? listing.binPrice * purchasing : listing.binPrice;

			if(listing.ownerUUID.equals(player.getUniqueId())) {
				binLoreBuilder.addLore("&cYou cannot buy your own listing!");
			} else if(PitPlayer.getPitPlayer(player).taintedSouls < cost) {
				binLoreBuilder.addLore("&cNot enough souls!");
				if(purchasing != 1) binLoreBuilder.addLore("&eRight-Click to change purchase amount");
			} else {
				if(!listing.stackBIN)binLoreBuilder.addLore("&eClick to buy for &f" + cost + " Souls");
				else {
					binLoreBuilder.addLore("&eLeft-Click to buy &f" + purchasing + " Item(s) &efor &f" + cost + " Souls");
					binLoreBuilder.addLore("&eRight-Click to change purchase amount");
				}
			}
		} else {
			binLoreBuilder.addLore("&7BIN is not enabled for this item!");
		}

		binBuilder.setLore(binLoreBuilder);
		getInventory().setItem(15, binBuilder.getItemStack());

		AItemStackBuilder backBuilder = new AItemStackBuilder(Material.BARRIER)
				.setName("&cBack");
		getInventory().setItem(31, backBuilder.getItemStack());

		AItemStackBuilder bidsBuilder = new AItemStackBuilder(Material.MAP)
				.setName("&eTop Bids");
		ALoreBuilder bidsLoreBuilder = new ALoreBuilder();
		for(Map.Entry<UUID, Integer> entry : listing.sortBidMap()) {
			bidsLoreBuilder.addLore(listing.bidderDisplayNames.get(entry.getKey()) + "&: &f" + entry.getValue() + " Souls");
		}

		bidsBuilder.setLore(bidsLoreBuilder);
		bidsBuilder.getItemStack().getItemMeta().addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		if(listing.startingBid != -1) getInventory().setItem(22, bidsBuilder.getItemStack());

	}

	@Override
	public String getName() {
		return "Inspect Listing";
	}

	@Override
	public int getRows() {
		return 4;
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		if(event.getClickedInventory().getHolder() != this) return;
		int slot = event.getSlot();

		if(slot == 11) {
			if(listing.startingBid != -1) {
				if(listing.ownerUUID.equals(player.getUniqueId()) || (listing.getHighestBidder() != null && listing.getHighestBidder().equals(player.getUniqueId())) || PitPlayer.getPitPlayer(player).taintedSouls < soulsToTake) {
					Sounds.NO.play(player);
				} else {
					if(event.isRightClick()) {
						bidSign();
						return;
					}

					ConfirmPurchasePanel panel = new ConfirmPurchasePanel(gui, listing, soulsToTake, false, 1);
					openPanel(panel);
				}
			}

		} else if(slot == 15) {

			if(event.isRightClick() && listing.stackBIN) {
				binSign();
				return;
			}

			if(listing.binPrice != -1) {
				if(listing.ownerUUID.equals(player.getUniqueId()) || PitPlayer.getPitPlayer(player).taintedSouls < (listing.stackBIN ? listing.binPrice * purchasing : listing.binPrice)) {
					Sounds.NO.play(player);
				} else {
					ConfirmPurchasePanel panel = new ConfirmPurchasePanel(gui, listing, listing.stackBIN ? purchasing * listing.binPrice : listing.binPrice, true, purchasing);
					openPanel(panel);
				}
			}

		} else if(slot == 31) {
			if(marketPanel) openPanel(((MarketGUI) gui).marketPanel);
			else openPanel(((MarketGUI) gui).yourListingsPanel);
		}
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
		runnable = new BukkitRunnable() {
			@Override
			public void run() {
				calculateItems();
			}
		}.runTaskTimer(PitSim.INSTANCE, 20, 20);
	}

	@Override
	public void onClose(InventoryCloseEvent event) {
		if(runnable != null) runnable.cancel();
	}

	public void binSign() {

		SignPrompt.promptPlayer(player, "", "^^^^^", "Enter buy amount", "(Max " + listing.itemData.getAmount() + ")", input -> {
			openPanel(this);
			int amount;
			try {
				amount = Integer.parseInt(input.replaceAll("\"", ""));
			} catch(Exception ignored) {
				Sounds.NO.play(player);
				AOutput.error(player, "&c&lERROR!&7 Could not parse amount!");
				return;
			}

			if(amount < 1 || amount > listing.itemData.getAmount()) {
				Sounds.NO.play(player);
				AOutput.error(player, "&c&lERROR!&7 Invalid amount!");
				return;
			}

			purchasing = amount;
			openPanel(this);
			calculateItems();
		});
	}

	public void bidSign() {

		SignPrompt.promptPlayer(player, "", "^^^^^", "Enter bid amount", "(Min " + listing.getMinimumBid() + ")", input -> {
			openPanel(this);
			int amount;
			try {
				amount = Integer.parseInt(input.replaceAll("\"", ""));
			} catch(Exception ignored) {
				Sounds.NO.play(player);
				AOutput.error(player, "&c&lERROR!&7 Could not parse amount!");
				return;
			}

			if(amount < listing.getMinimumBid()) {
				Sounds.NO.play(player);
				AOutput.error(player, "&c&lERROR!&7 Bid is less than minimum!");
				return;
			}

			if(amount > PitPlayer.getPitPlayer(player).taintedSouls) {
				Sounds.NO.play(player);
				AOutput.error(player, "&c&lERROR!&7 You do not have enough souls!");
				return;
			}

			bid = amount;

			soulsToTake = bid - listing.bidMap.getOrDefault(player.getUniqueId(), 0);

			ConfirmPurchasePanel panel = new ConfirmPurchasePanel(gui, listing, soulsToTake, false, 1);
			openPanel(panel);
		});
	}
}
