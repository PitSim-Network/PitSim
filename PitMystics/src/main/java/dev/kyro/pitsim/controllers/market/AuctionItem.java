package dev.kyro.pitsim.controllers.market;

import dev.kyro.pitsim.misc.ItemBase64;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class AuctionItem implements Serializable {

	public UUID uuid;
	public String playerName;
	public UUID playerUUID;

	public ItemStack item;
	public Date dateListed;
	public long duration;
	public List<Currency> acceptedCurrencies;
	public MarketOrder BIN;

	public String topBidderName;
	public UUID topBidderUUID;
	public MarketOrder topBid;

	public AuctionItem(Player player, ItemStack item, long duration, List<Currency> acceptedCurrencies, MarketOrder BIN) {

		this.uuid = UUID.randomUUID();
		this.playerName = player.getName();
		this.playerUUID = player.getUniqueId();

		this.item = item;
		this.dateListed = new Date();
		this.duration = duration;
		this.acceptedCurrencies = acceptedCurrencies;
		this.BIN = BIN;
	}

	public AuctionItem(ConfigurationSection save) {
		this.uuid = UUID.fromString(save.getString("uuid"));
		this.playerName = save.getString("player-name");
		this.playerUUID = UUID.fromString(save.getString("player-uuid"));

		try {
			this.item = ItemBase64.itemFrom64(save.getString("item"));
		} catch(IOException e) {
			e.printStackTrace();
		}

		this.dateListed = new Date(save.getLong("date-listed"));
		this.duration = save.getLong("duration");

		List<Currency> acceptedCurrencies = new ArrayList<>();
		for(String string : save.getString("accepted-currencies").split(" ")) acceptedCurrencies.add(Currency.getCurrency(string));
		this.acceptedCurrencies = acceptedCurrencies;

		this.BIN = new MarketOrder(save.getConfigurationSection("bin"));

		this.topBidderName = save.getString("top-bidder");
		this.topBidderUUID = UUID.fromString(save.getString("bidder-uuid"));
		this.topBid = new MarketOrder(save.getConfigurationSection("top-bid"));
	}

	public ConfigurationSection createSave() {
		ConfigurationSection save = new MemoryConfiguration();

		save.set("uuid", uuid.toString());
		save.set("player-name", playerName);
		save.set("player-uuid", playerUUID.toString());
		save.set("item", ItemBase64.itemTo64(item));
		save.set("date-listed", dateListed.getTime());
		save.set("duration", duration);

		List<String> currencyRefs = new ArrayList<>();
		for(Currency currency : acceptedCurrencies) currencyRefs.add(currency.refName);
		save.set("accepted-currencies", String.join(" ", currencyRefs));

		save.set("bin", BIN.createSave());
		save.set("top-bidder", topBidderName);
		save.set("bidder-uuid", topBidderUUID.toString());
		save.set("top-bid", topBid.createSave());

		return save;
	}

	public boolean isActive() {
		return dateListed.getTime() + duration > new Date().getTime();
	}
}
