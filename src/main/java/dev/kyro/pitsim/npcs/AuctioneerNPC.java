package dev.kyro.pitsim.npcs;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.objects.PitNPC;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

public class AuctioneerNPC extends PitNPC {

	public AuctioneerNPC(List<World> worlds) {
		super(worlds);
	}

	@Override
	public Location getRawLocation() {
		return new Location(MapManager.getDarkzone(), 247.5, 91, 8.5, 145, 0);
	}

	@Override
	public void createNPC(Location location) {
		spawnPlayerNPC("&8&lSHADY FIGURE", "Itz_Aethan", location, true);
	}

	@Override
	public void onClick(Player player) {
		AOutput.send(player, "&8&lSHADY FIGURE&7: &ePsst. Visit the door behind me to spend your &fTainted Souls&e.");
	}
}
