package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.builders.AInventoryBuilder;
import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.gui.AInventoryGUI;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.market.AuctionItem;
import dev.kyro.pitsim.controllers.market.Currency;
import dev.kyro.pitsim.controllers.market.MarketManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.List;

public class AuctionGUI extends AInventoryGUI {

	public Player player;
	public AInventoryBuilder builder;
	public int page = 1;

	public AuctionGUI(Player player) {
		super("AH", 6);

		this.player = player;
		builder = new AInventoryBuilder(baseGUI)
				.createBorder(Material.STAINED_GLASS_PANE, 4);

		baseGUI.setItem(45, new AItemStackBuilder(Material.ARROW, 1).setName("&6Previous Page").getItemStack());
		baseGUI.setItem(49, new AItemStackBuilder(Material.EYE_OF_ENDER, 1).setName("&bPage 1&7/&b1").getItemStack());
		baseGUI.setItem(53, new AItemStackBuilder(Material.ARROW, 1).setName("&6Next Page").getItemStack());

		int count = 0;
		for(int i = 0; count != MarketManager.auctionItems.size(); i++) {

			if(i < 9 || i % 9 == 0 || i % 9 == 8) continue;

			AuctionItem auctionItem = MarketManager.auctionItems.get(count++);
			if(!auctionItem.isActive()) {
				i--;
				continue;
			}

			ItemStack itemStack = auctionItem.item.clone();
			ItemMeta itemMeta = itemStack.getItemMeta();
			DecimalFormat df = new DecimalFormat("0.#");

			List<String> lore = new ALoreBuilder(itemStack).getLore();
			lore.add(0, "");
			lore.add(0, "&7BIN: &9" +
					(auctionItem.BIN != null ? df.format(auctionItem.BIN.getTotalWorth(Currency.PURE)) + " Pure" : "&aNone"));
			lore.add(0, "&7C/O: &9" +
					(auctionItem.topBid != null ? df.format(auctionItem.topBid.getTotalWorth(Currency.PURE)) + " Pure" : "&aNone"));

			itemMeta.setLore(new ALoreBuilder(lore).getLore());
			itemStack.setItemMeta(itemMeta);

			baseGUI.setItem(i, itemStack);
		}
	}

	@Override
	public void onClick(InventoryClickEvent event) {

	}

	@Override
	public void onOpen(InventoryOpenEvent event) {

		new BukkitRunnable() {
			@Override
			public void run() {
				updateGUI();
			}
		}.runTaskLater(PitSim.INSTANCE, 1L);
	}

	@Override
	public void onClose(InventoryCloseEvent event) {

	}

	public void updateGUI() {

		for(int i = 0; i < baseGUI.getSize(); i++) {
			player.getOpenInventory().setItem(i, baseGUI.getItem(i));
		}
	}
}
