//package dev.kyro.pitsim.controllers.market;
//
//import de.tr7zw.nbtapi.NBTItem;
//import dev.kyro.arcticapi.builders.AItemStackBuilder;
//import dev.kyro.arcticapi.data.APlayerData;
//import dev.kyro.pitsim.PitSim;
//import dev.kyro.pitsim.controllers.NonManager;
//import org.bukkit.Material;
//import org.bukkit.configuration.file.FileConfiguration;
//import org.bukkit.entity.Player;
//import org.bukkit.event.EventHandler;
//import org.bukkit.event.Listener;
//import org.bukkit.event.player.PlayerJoinEvent;
//import org.bukkit.inventory.ItemStack;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Set;
//import java.util.UUID;
//
//public class MarketManager implements Listener {
//
//	public static List<AuctionItem> auctionItems = new ArrayList<>();
//
//	static {
//
//		List<String> playerList = PitSim.playerList.getStringList("players");
//		if(playerList != null) {
//			for(String player : playerList) {
//				if(NonManager.getNon(player) != null) continue;
//				FileConfiguration playerData = APlayerData.getPlayerData(UUID.fromString(player));
//				if(playerData == null || playerData.getConfigurationSection("auctions") == null) continue;
//				Set<String> keys = playerData.getConfigurationSection("auctions").getKeys(false);
//				for(String key : keys) {
//					System.out.println(key);
//					auctionItems.add(new AuctionItem(playerData.getConfigurationSection("auctions." + key)));
//				}
//			}
//		}
//	}
//
//	@EventHandler
//	public static void onJoin(PlayerJoinEvent event) {
//
//		List<String> playerList = PitSim.playerList.getStringList("players");
//		playerList = playerList != null ? playerList : new ArrayList<>();
//		if(playerList.contains(event.getPlayer().getUniqueId().toString())) return;
//		playerList.add(event.getPlayer().getUniqueId().toString());
//		PitSim.playerList.set("players", playerList);
//		PitSim.playerList.saveDataFile();
//	}
//
//	public static void addItem(Player player, AuctionItem auctionItem) {
//		auctionItems.add(auctionItem);
//
//		FileConfiguration playerData = APlayerData.getPlayerData(player);
//		playerData.set("auctions." + auctionItem.uuid.toString(), auctionItem.createSave());
//		APlayerData.savePlayerData(player);
//	}
//
//	public static ItemStack createValue(Currency currency, int amount) {
//
//		AItemStackBuilder value = null;
//		switch(currency) {
//			case PURE:
//				value = new AItemStackBuilder(Material.STORAGE_MINECART, amount)
//						.setName("&bPants Bundle");
//				break;
//			case PHILO:
//				value = new AItemStackBuilder(Material.CACTUS, amount)
//						.setName("&aPhilosopher's Cactus");
//				break;
//			case FEATHER:
//				value = new AItemStackBuilder(Material.FEATHER, amount)
//						.setName("&3Funky Feather");
//				break;
//			case WATER:
//				value = new AItemStackBuilder(Material.WATER_BUCKET, amount)
//						.setName("&9Water");
//				break;
//		}
//
//		NBTItem nbtItem = new NBTItem(value.getItemStack());
//		nbtItem.setInteger(currency.refName, amount);
//
//		return nbtItem.getItem();
//	}
//}
