package net.pitsim.pitsim.tutorial;

import net.pitsim.pitsim.controllers.MapManager;
import org.bukkit.Location;

 public enum Midpoint {
	ENTRANCE(new Location(MapManager.getDarkzone(), 189.5, 91, -93.5, 13, 0)),
	SPAWN1(new Location(MapManager.getDarkzone(), 202.5, 91, -93.5)),
 	SPAWN2(new Location(MapManager.getDarkzone(), 218.5, 91, -93)),
	EXIT(new Location(MapManager.getDarkzone(), 235.5, 91, -93.5)),
	PATH1(new Location(MapManager.getDarkzone(), 257.5, 91, -104.5)),
	PATH2(new Location(MapManager.getDarkzone(), 272.5, 91, -116.5)),
	;

	public final Location location;

	Midpoint(Location location) {
		this.location = location;
	}
}
