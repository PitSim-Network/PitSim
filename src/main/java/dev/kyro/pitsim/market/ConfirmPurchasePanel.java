package dev.kyro.pitsim.market;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class ConfirmPurchasePanel extends AGUIPanel {
	public MarketListing listing;
	public int price;
	public boolean bin;
	public int amount;

	public ConfirmPurchasePanel(AGUI gui, MarketListing listing, int price, boolean bin, int amount) {
		super(gui);

		this.listing = listing;
		this.price = price;
		this.bin = bin;
		this.amount = amount;

		AItemStackBuilder confirmBuilder = new AItemStackBuilder(Material.STAINED_CLAY, 1, 5)
				.setName("&a&lCONFIRM " + (bin ? "BIN" : "BID"))
				.setLore(new ALoreBuilder(
						"&7Purchasing: " + listing.itemData.getItemMeta().getDisplayName() + (bin ? " &8x" + amount : ""),
						"&7Price: &f" + price + " Souls", "",
						"&eClick to confirm purchase!"
				));
		getInventory().setItem(11, confirmBuilder.getItemStack());

		int soulsToTake = price - listing.bidMap.getOrDefault(player.getUniqueId(), 0);

		AItemStackBuilder cancelBuilder = new AItemStackBuilder(Material.STAINED_CLAY, 1, 14)
				.setName("&c&lCANCEL")
				.setLore(new ALoreBuilder(
						"&7Purchasing: " + listing.itemData.getItemMeta().getDisplayName() + (bin ? " &8x" + amount : ""),
						"&7Price: &f" + soulsToTake + " Souls", "",
						"&eClick to cancel purchase!"
				));
		getInventory().setItem(15, cancelBuilder.getItemStack());
	}

	@Override
	public String getName() {
		return "Confirm Purchase?";
	}

	@Override
	public int getRows() {
		return 3;
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		MarketAsyncTask.MarketTask task = bin ? MarketAsyncTask.MarketTask.BIN_ITEM : MarketAsyncTask.MarketTask.PLACE_BID;

		String failMessage = "&cThere was an error while attempting to process your transaction. Please try again in a moment.";

		BukkitRunnable bid = new BukkitRunnable() {
			@Override
			public void run() {
				AOutput.send(player, "&a&lMARKET &7Bid successfully placed!");
				Sounds.SUCCESS.play(player);
				int soulsToTake = price - listing.bidMap.getOrDefault(player.getUniqueId(), 0);

				PitPlayer.getPitPlayer(player).taintedSouls -= soulsToTake;
				player.closeInventory();
			}
		};

		BukkitRunnable buy = new BukkitRunnable() {
			@Override
			public void run() {
				AOutput.send(player, "&a&lMARKET &7Purchased " + listing.itemData.getItemMeta().getDisplayName() + (amount > 1 ? " &8x" + amount : ""));
				PitPlayer.getPitPlayer(player).taintedSouls -= price;

				ItemStack item = listing.itemData.clone();
				item.setAmount(amount);

				AUtil.giveItemSafely(player, item, true);
				Sounds.SUCCESS.play(player);
				player.closeInventory();
			}
		};


		if(event.getSlot() == 11) {
			new MarketAsyncTask(task, listing.marketUUID, player, (bin ? amount : price), bin ? buy : bid, MarketAsyncTask.getDefaultFail(player, failMessage));
		}

		if(event.getSlot() == 15) openPreviousGUI();
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {

	}

	@Override
	public void onClose(InventoryCloseEvent event) {

	}
}
