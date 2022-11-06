package dev.kyro.pitsim.anpcs;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.objects.PitNPC;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.inventories.PerkGUI;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

public class UpgradeNPC extends PitNPC {

	public UpgradeNPC(List<World> worlds) {
		super(worlds);
	}

	@Override
	public Location getRawLocation() {
		return null;
	}

	@Override
	public Location getFinalLocation(World world) {
		return MapManager.currentMap.getUpgradeNPCSpawn(world);
	}

	@Override
	public void createNPC(Location location) {
		spawnVillagerNPC("", location);
	}

	@Override
	public void onClick(Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		if(pitPlayer.megastreak.isOnMega()) {
			AOutput.error(player, "&c&lERROR!&7 You cannot use this command while on a megastreak!");
			return;
		}

		PerkGUI perkGUI = new PerkGUI(player);
		perkGUI.open();
	}
}
