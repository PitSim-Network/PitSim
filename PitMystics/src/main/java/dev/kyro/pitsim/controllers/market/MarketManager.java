package dev.kyro.pitsim.controllers.market;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.pitsim.PitSim;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MarketManager {

	public static List<AuctionItem> auctionItems = new ArrayList<>();

	static {

		List<String> playerList = PitSim.playerList.getStringList("players");
		if(playerList != null) {
			for(String player : playerList) {
				FileConfiguration playerData = APlayerData.getPlayerData(UUID.fromString(player));
				List<AuctionItem> playerAuctions = (List<AuctionItem>) playerData.getList("auctions");
				if(playerAuctions == null) continue;
				auctionItems.addAll(playerAuctions);
			}
		}
	}

	public static ItemStack createValue(Currency currency, int amount) {

		AItemStackBuilder value = null;
		switch(currency) {
			case PURE:
				value = new AItemStackBuilder(Material.STORAGE_MINECART, amount)
						.setName("&bPants Bundle");
				break;
			case PHILO:
				value = new AItemStackBuilder(Material.CACTUS, amount)
						.setName("&aPhilosopher's Cactus");
				break;
			case FEATHER:
				value = new AItemStackBuilder(Material.FEATHER, amount)
						.setName("&3Funky Feather");
				break;
			case WATER:
				value = new AItemStackBuilder(Material.WATER_BUCKET, amount)
						.setName("&9Water");
				break;
		}

		NBTItem nbtItem = new NBTItem(value.getItemStack());
		nbtItem.setInteger(currency.refName, amount);

		return nbtItem.getItem();
	}
}
