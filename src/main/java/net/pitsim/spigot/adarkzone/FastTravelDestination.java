package net.pitsim.spigot.adarkzone;

import dev.kyro.arcticapi.misc.AOutput;
import net.pitsim.spigot.controllers.ItemFactory;
import net.pitsim.spigot.misc.Sounds;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

public class FastTravelDestination {
	public Location location;
	public int cost;
	public String displayName;
	public SubLevel subLevel;
	public MaterialData icon;

	public FastTravelDestination(String displayName, Location location, int cost, MaterialData icon) {
		this.location = location;
		this.cost = cost;
		this.displayName = displayName;
		this.icon = icon;
		this.subLevel = null;
	}

	public FastTravelDestination(SubLevel subLevel) {
		this.subLevel = subLevel;
		this.location = subLevel.getSpawnLocation();
		this.cost = DarkzoneBalancing.getTravelCost(subLevel);
		this.displayName = DarkzoneManager.getDummyMob(subLevel.getMobClass()).getDisplayName().
				replaceAll("\u00A70", "\u00A78") + " Caves";
		this.icon = ItemFactory.getItem(subLevel.getSpawnItemClass()).getItem().getData();
	}

	public void travel(Player player) {
		player.teleport(location);
		AOutput.send(player, "&f&lFAST TRAVEL!&7 You have been sent to " + displayName);
		Sounds.FAST_TRAVEL.play(player);
	}
}
