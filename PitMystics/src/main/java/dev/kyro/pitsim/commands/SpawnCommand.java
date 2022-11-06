package dev.kyro.pitsim.commands;

import dev.kyro.pitsim.controllers.HopperManager;
import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.SpawnManager;
import dev.kyro.pitsim.controllers.objects.Hopper;
import dev.kyro.pitsim.events.PlayerSpawnCommandEvent;
import dev.kyro.pitsim.helmetabilities.PhoenixAbility;
import dev.kyro.pitsim.perks.Streaker;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

//TODO: not allow on streaks
public class SpawnCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;

		PlayerSpawnCommandEvent event = new PlayerSpawnCommandEvent(player);
		Bukkit.getPluginManager().callEvent(event);
		if(event.isCancelled() && !player.isOp()) return false;

		SpawnManager.lastLocationMap.remove(player);
		Location teleportLoc = MapManager.currentMap.getSpawn(player.getWorld());
		player.teleport(teleportLoc);
		PhoenixAbility.alreadyActivatedList.remove(player.getUniqueId());
		for(Hopper hopper : HopperManager.hopperList) {
			if(player != hopper.target) continue;
			hopper.remove();
		}

		Streaker.xpReward.remove(player);
		Streaker.playerTimes.remove(player);

		return false;
	}
}
