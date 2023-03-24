package dev.kyro.pitsim.adarkzone;

import dev.kyro.pitsim.controllers.MapManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.List;

public class FastTravelManager { 
	public static List<FastTravelDestination> destinations = new ArrayList<>();
	
	public static void init() {
		for(SubLevel subLevel : DarkzoneManager.subLevels) {
			destinations.add(new FastTravelDestination(subLevel));
		}

		Location darkAuctionLocation = new Location(MapManager.getDarkzone(), 244.5, 91, 4.5);
		MaterialData darkAuctionIcon = new MaterialData(Material.WATCH, (byte) 0);
		destinations.add(new FastTravelDestination("&5Dark Auction", darkAuctionLocation, 2, darkAuctionIcon));

		Location spawn = MapManager.getDarkzoneSpawn();
		MaterialData spawnIcon = new MaterialData(Material.BED, (byte) 0);
		destinations.add(new FastTravelDestination("&aSpawn", spawn, 2, spawnIcon));
	}
}
