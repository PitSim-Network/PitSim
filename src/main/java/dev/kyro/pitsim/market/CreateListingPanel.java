package dev.kyro.pitsim.market;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.controllers.objects.PluginMessage;
import dev.kyro.pitsim.misc.Sounds;
import dev.kyro.pitsim.misc.packets.SignPrompt;
import dev.kyro.pitsim.storage.StorageProfile;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

public class CreateListingPanel extends AGUIPanel {

	public ItemStack selectedItem = null;
	public boolean auctionEnabled = false;
	public boolean binEnabled = false;

	public int startingBid = 0;
	public int binPrice = 0;

	public boolean signClose = false;

	public CreateListingPanel(AGUI gui) {
		super(gui);

		for(int i = 0; i < 5 * 9; i++) {
			getInventory().setItem(i, new AItemStackBuilder(Material.STAINED_GLASS_PANE, 1, 15).setName(" ").getItemStack());
			calculateItems();
		}
	}

	@Override
	public String getName() {
		return "Create Market Listing";
	}

	@Override
	public int getRows() {
		return 5;
	}

	@Override
	public void onClick(InventoryClickEvent event) {

		int slot = event.getSlot();
		if(event.getClickedInventory().getHolder() == this) {
			if(slot == 13) {
				if(selectedItem == null) return;
				AUtil.giveItemSafely(player, selectedItem, true);
				Sounds.BOOSTER_REMIND.play(player);
				auctionEnabled = false;
				binEnabled = false;
				startingBid = 0;
				binPrice = 0;
				selectedItem = null;
				calculateItems();
			}

			if(slot == 11) {
				if(selectedItem == null) {
					AOutput.error(player, "&cSelect an item first!");
					Sounds.NO.play(player);
					return;
				}

				if(selectedItem.getAmount() > 1) {
					AOutput.error(player, "&cYou can only auction one item at a time!");
					Sounds.NO.play(player);
					return;
				}

				auctionEnabled = !auctionEnabled;
				Sounds.HELMET_TICK.play(player);
				calculateItems();
			}

			if(slot == 15) {
				if(selectedItem == null) {
					AOutput.error(player, "&cSelect an item first!");
					Sounds.NO.play(player);
					return;
				}

				binEnabled = !binEnabled;
				Sounds.HELMET_TICK.play(player);
				calculateItems();
			}

			if(slot == 20) {
				if(!auctionEnabled) {
					Sounds.NO.play(player);
					return;
				}

				signPromptAuction();
			}

			if(slot == 24) {
				if(!binEnabled) {
					Sounds.NO.play(player);
					return;
				}

				signPromptBin();
			}

			if(slot == 38) {
				openPanel(((MarketGUI) gui).selectionPanel);
			}

			if(slot == 40) {
				if(selectedItem == null || (!auctionEnabled && !binEnabled) || (auctionEnabled && !isBidValid()) || (binEnabled && !isBinValid())) {
					Sounds.NO.play(player);
					return;
				}

				PluginMessage message = new PluginMessage().writeString("CREATE LISTING").writeString(player.getUniqueId().toString());
				message.writeString(StorageProfile.serialize(player, selectedItem));
				message.writeInt(startingBid == 0 ? -1 : startingBid).writeInt(binPrice == 0 ? -1 : binPrice).writeBoolean(selectedItem.getAmount() > 1).writeLong(86400000L * getMaxDurationDays()).send();
				selectedItem = null;
				Sounds.SUCCESS.play(player);
				AOutput.send(player, "&a&lMARKET &7Listing created!");
				player.closeInventory();
			}
		}

		if(event.getClickedInventory().equals(player.getInventory())) {
			ItemStack item = event.getCurrentItem();

			if(item == null || item.getType() == Material.AIR || !item.hasItemMeta()) {
				Sounds.NO.play(player);
			} else {

				if(selectedItem != null) {
					AUtil.giveItemSafely(player, selectedItem, true);
				}

				auctionEnabled = false;
				binEnabled = false;
				startingBid = 0;
				binPrice = 0;
				selectedItem = item;
				Sounds.BOOSTER_REMIND.play(player);
				player.getInventory().setItem(slot, new ItemStack(Material.AIR));
				player.updateInventory();
				calculateItems();
			}
		}
	}

	public void calculateItems() {

		AItemStackBuilder selectedItemBuilder = new AItemStackBuilder(Material.ITEM_FRAME)
				.setName("&eSelect an Item!")
				.setLore(new ALoreBuilder(
						"&7Click on an item in your",
						"&7inventory to select it."
				));
		getInventory().setItem(13, selectedItem == null ? selectedItemBuilder.getItemStack() : selectedItem);

		AItemStackBuilder auctionBuilder;
		if(auctionEnabled && isBidValid()) {
			auctionBuilder = new AItemStackBuilder(Material.STAINED_CLAY, 1, 13)
					.setName("&eSell Item as Auction")
					.setLore(new ALoreBuilder(
							"&7Players will be able to bid",
							"&7on your item", "",
							"&eClick to disable auction"
					));
		} else if(auctionEnabled) {
			auctionBuilder = new AItemStackBuilder(Material.STAINED_CLAY, 1, 4)
					.setName("&6Sell Item as Auction")
					.setLore(new ALoreBuilder(
							"&7Your starting bid is &cINVALID!",
							"&7Correct it below", "",
							"&6Click to disable auction"
					));
		} else if(selectedItem != null && selectedItem.getAmount() > 1) {
			auctionBuilder = new AItemStackBuilder(Material.BARRIER)
					.setName("&cSell Item as Auction")
					.setLore(new ALoreBuilder(
							"&7Players will be able to bid",
							"&7on your item", "",
							"&cCannot auction multiple items!"
					));
		} else {
			auctionBuilder = new AItemStackBuilder(Material.STAINED_CLAY, 1, 14)
					.setName("&eSell Item as Auction")
					.setLore(new ALoreBuilder(
							"&7Players will be able to bid",
							"&7on your item", "",
							"&eClick to enable auction"
					));
		}
		getInventory().setItem(11, auctionBuilder.getItemStack());

		AItemStackBuilder binBuilder;
		if(binEnabled && isBinValid()) {
			binBuilder = new AItemStackBuilder(Material.STAINED_CLAY, 1, 13)
					.setName("&eSell Item as BIN")
					.setLore(new ALoreBuilder(
							"&7Players will be able to buy",
							"&7your item instantly", "",
							"&eClick to disable BIN"
					));
		} else if(binEnabled) {
			binBuilder = new AItemStackBuilder(Material.STAINED_CLAY, 1, 4)
					.setName("&6Sell Item as BIN")
					.setLore(new ALoreBuilder(
							"&7Your BIN price is &cINVALID!",
							"&7Correct it below", "",
							"&6Click to disable BIN"
					));
		} else {
			binBuilder = new AItemStackBuilder(Material.STAINED_CLAY, 1, 14)
					.setName("&eSell Item as BIN")
					.setLore(new ALoreBuilder(
							"&7Players will be able to buy",
							"&7your item instantly", "",
							"&eClick to enable BIN"
					));
		}
		getInventory().setItem(15, binBuilder.getItemStack());

		AItemStackBuilder auctionSoulsBuilder = new AItemStackBuilder(Material.INK_SACK, 1, 7)
				.setName("&eSet Starting Bid")
				.setLore(new ALoreBuilder(
						"&7Starting Bid: " + (isBidValid() ? "&f" + startingBid + " souls" : "&cINVALID!"),
						"",
						"&7Must be at least &f10 Souls", "",
						auctionEnabled ? "&eClick to change!" : "&cEnable auction first!"
				));
		getInventory().setItem(20, auctionSoulsBuilder.getItemStack());

		AItemStackBuilder binSoulsBuilder = new AItemStackBuilder(Material.INK_SACK, 1, 7)
				.setName("&eSet BIN Price")
				.setLore(new ALoreBuilder(
						"&7BIN Price: " + (isBinValid() ? ("&f" + binPrice + " souls" + (selectedItem.getAmount() > 1 ? " &8(Per Item)" : "")) : "&cINVALID!"),
						"",
						binEnabled && isBidValid() ? "&7Must be at least &f" + (startingBid * 2) + " Souls" : "&7Must be at least &f10 Souls", "",
						binEnabled ? "&eClick to change!" : "&cEnable BIN first!"
				));
		getInventory().setItem(24, binSoulsBuilder.getItemStack());

		AItemStackBuilder durationBuilder = new AItemStackBuilder(Material.WATCH)
				.setName("&eListing Duration")
				.setLore(new ALoreBuilder(
						"&7This listing will expire in:",
						"&f" + getMaxDurationDays() + " Days", "",
						"&eBoost this duration with a rank", "&efrom &f&nstore.pitsim.net"
				));
		getInventory().setItem(42, durationBuilder.getItemStack());

		AItemStackBuilder backBuilder = new AItemStackBuilder(Material.BARRIER)
				.setName("&cCancel")
				.setLore(new ALoreBuilder(
						"&7Go back to the previous menu"
				));
		getInventory().setItem(38, backBuilder.getItemStack());


		boolean invalid = (auctionEnabled && !isBidValid() )|| (binEnabled && !isBinValid());

		AItemStackBuilder confirmBuilder;
		if(selectedItem == null || (!auctionEnabled && !binEnabled)) {
			confirmBuilder = new AItemStackBuilder(Material.REDSTONE_BLOCK)
					.setName("&cConfirm Listing")
					.setLore(new ALoreBuilder(
							"&7Click to confirm your listing", "",
							"&cSelect a Sell Type first!"
					));
		} else if(invalid) {
			confirmBuilder = new AItemStackBuilder(Material.STAINED_CLAY, 1, 1)
					.setName("&6Confirm Listing")
					.setLore(new ALoreBuilder(
							"&7Click to confirm your listing", "",
							"&6Fix invalid Sell Values!"
					));
		} else {
			confirmBuilder = new AItemStackBuilder(Material.EMERALD_BLOCK)
					.setName("&aConfirm Listing")
					.setLore(new ALoreBuilder(
							"&7Click to confirm your listing", "",
							"&aClick to confirm!"
					));
		}
		getInventory().setItem(40, confirmBuilder.getItemStack());


	}

	@Override
	public void onOpen(InventoryOpenEvent event) {

	}

	@Override
	public void onClose(InventoryCloseEvent event) {
		if(selectedItem != null && !signClose) {
			Player player = (Player) event.getPlayer();
			AUtil.giveItemSafely(player, selectedItem, true);
			player.updateInventory();
		}

		signClose = false;
	}

	public boolean isBidValid() {
		return startingBid >= 10 && startingBid <= 10000;
	}

	public boolean isBinValid() {
		return binPrice >= 10 && binPrice >= startingBid * 2 && binPrice <= 10000;
	}

	public void signPromptAuction() {

		signClose = true;
		SignPrompt.promptPlayer(player, "", "^^^^^", "Enter the Starting", "Bid (At least 10)", input -> {
			openPanel(this);
			int amount;
			try {
				amount = Integer.parseInt(input.replaceAll("\"", ""));
			} catch(Exception ignored) {
				Sounds.NO.play(player);
				AOutput.error(player, "&c&lERROR!&7 Could not parse bid!");
				return;
			}

			startingBid = amount;
			calculateItems();
			if(!isBidValid()) {
				Sounds.NO.play(player);
				AOutput.error(player, "&c&lERROR!&7 Bid must be at least 10 souls!");
			}
		});
	}

	public void signPromptBin() {

		signClose = true;
		SignPrompt.promptPlayer(player, "", "^^^^^", "Enter BIN price", auctionEnabled ? "(2x Starting Bid)" : "(At least 10)", input -> {
			openPanel(this);
			int amount;
			try {
				amount = Integer.parseInt(input.replaceAll("\"", ""));
			} catch(Exception ignored) {
				Sounds.NO.play(player);
				AOutput.error(player, "&c&lERROR!&7 Could not parse bid!");
				return;
			}

			binPrice = amount;
			calculateItems();
			if(!isBinValid()) {
				Sounds.NO.play(player);
				if(!auctionEnabled) AOutput.error(player, "&c&lERROR!&7 BIN must be at least 10 souls!");
				else AOutput.error(player, "&c&lERROR!&7 BIN must be at least 2x the starting bid!");
			}
		});
	}

	public int getMaxDurationDays() {
		return 3;
	}

	public static long parseToMiliseconds(String duration) {
		String[] parts = duration.split(" ");
		int days = Integer.parseInt(parts[0].replace("d", ""));
		int hours = Integer.parseInt(parts[2].replace("h", ""));
		int minutes = Integer.parseInt(parts[4].replace("m", ""));

		return (days * 86400L + hours * 3600L + minutes * 60L) * 1000;
	}
}
