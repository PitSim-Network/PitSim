package dev.kyro.pitsim.commands;

import dev.kyro.arcticapi.misc.AOutput;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReloadCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;
		if(!player.isOp()) {
			AOutput.error(player, "&cInsufficient Permissions");
			return false;
		}

		Bukkit.getServer().dispatchCommand(player, "plugman unload pitdiscord");
		Bukkit.getServer().dispatchCommand(player, "plugman reload pitremake");
		Bukkit.getServer().dispatchCommand(player, "plugman load pitdiscord");

		return false;
	}
}
