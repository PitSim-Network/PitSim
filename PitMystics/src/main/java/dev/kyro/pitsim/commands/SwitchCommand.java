package dev.kyro.pitsim.commands;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.MapManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SwitchCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = (Player) sender;
		if(!player.isOp() && !player.hasPermission("pitsim.switch")) return false;

		World newWorld = MapManager.currentMap.getRandomOrFirst(player.getWorld());
		Location spawnLocation = MapManager.currentMap.getSpawn(newWorld);
		player.teleport(spawnLocation);
		AOutput.send(player, "&7You have connected to lobby &6" + (MapManager.currentMap.getLobbyIndex(spawnLocation.getWorld()) + 1));
		return false;
	}
}
