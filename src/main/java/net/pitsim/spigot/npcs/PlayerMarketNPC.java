package net.pitsim.spigot.npcs;

import dev.kyro.arcticapi.misc.AOutput;
import net.pitsim.spigot.PitSim;
import net.pitsim.spigot.controllers.MapManager;
import net.pitsim.spigot.controllers.objects.PitNPC;
import net.pitsim.spigot.market.MarketGUI;
import net.pitsim.spigot.misc.Sounds;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

public class PlayerMarketNPC extends PitNPC {

	public PlayerMarketNPC(List<World> worlds) {
		super(worlds);
	}

	@Override
	public Location getRawLocation() {
		return new Location(MapManager.getDarkzone(), 204, 91, -84.7, 180, 0);
	}

	@Override
	public void createNPC(Location location) {
		spawnPlayerNPC("", "Banker", location, false);
	}

	@Override
	public void onClick(Player player) {

		if(!PitSim.MARKET_ENABLED) {
			AOutput.error(player, "&a&lMARKET! &cThe player market is currently disabled!");
			Sounds.NO.play(player);
			return;
		}

		MarketGUI marketGUI = new MarketGUI(player);
		marketGUI.open();
	}
}
