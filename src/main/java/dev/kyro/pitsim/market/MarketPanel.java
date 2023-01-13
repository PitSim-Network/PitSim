package dev.kyro.pitsim.market;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.pitsim.misc.HeadLib;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.util.List;

public class MarketPanel extends AGUIPanel {

	public SortQuery sortQuery;
	public int page = 0;
	public int maxMages;

	public MarketPanel(AGUI gui) {
		super(gui);
		this.sortQuery = new SortQuery(SortQuery.PrimarySortType.PRICE_HIGH, SortQuery.SecondarySortType.ALL, "");

		MarketListing[] listings = sortQuery.getListings();
		maxMages = (int) Math.ceil(listings.length / 36D);

		int toAdd = page * 54;
		for(int i = 9; i < 44; i++) {
			if(i - 9 + toAdd >= listings.length) break;
			getInventory().setItem(i, listings[i - 9 + toAdd].itemData);
		}

		List<MarketListing> playerListings = MarketManager.getListings(player.getUniqueId());

		AItemStackBuilder downBuilder = new AItemStackBuilder(HeadLib.getCustomHead(HeadLib.getDownArrowHead()))
				.setName(page < maxMages - 1 ? "&aNext Page &7(" + page + 1 + "/" + maxMages + ")" : "&cNext Page &7(" + page + 1 + "/" + maxMages + ")");
		getInventory().setItem(48, downBuilder.getItemStack());

		AItemStackBuilder upBuilder = new AItemStackBuilder(HeadLib.getCustomHead(HeadLib.getDownArrowHead()))
				.setName(page > 0 ? "&aPrevious Page &7(" + page + 1 + "/" + maxMages + ")" : "&cPrevious Page &7(" + page + 1 + "/" + maxMages + ")");
		getInventory().setItem(50, upBuilder.getItemStack());

		AItemStackBuilder plusBuilder = new AItemStackBuilder(HeadLib.getCustomHead(HeadLib.getDownArrowHead()))
				.setName(playerListings.size() >= MarketManager.DEFAULT_MAX_LISTINGS ? "&cCreate Listing" : "&eCreate Listing");
		ALoreBuilder plusLore = new ALoreBuilder();
		if(playerListings.size() == 0) {
			plusLore.addLore("&7You have no listings");
		} else {
			for(MarketListing playerListing : playerListings) {
				plusLore.addLore("&7- " + playerListing.getItemStack().getItemMeta().getDisplayName());
			}
		}
		plusLore.addLore("", playerListings.size() >= MarketManager.DEFAULT_MAX_LISTINGS ? "&cClick to create" : "&eClick to create");
		plusBuilder.setLore(plusLore.getLore());
		getInventory().setItem(49, plusBuilder.getItemStack());
	}

	@Override
	public String getName() {
		return "Player Market";
	}

	@Override
	public int getRows() {
		return 6;
	}

	@Override
	public void onClick(InventoryClickEvent event) {

	}

	@Override
	public void onOpen(InventoryOpenEvent event) {

	}

	@Override
	public void onClose(InventoryCloseEvent event) {

	}
}
