package dev.kyro.pitsim.commands.essentials;

import dev.kyro.arcticapi.misc.AOutput;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FlyCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;
		if(!player.hasPermission("pitsim.flight")) return false;

		if(player.getAllowFlight()) {
			player.setAllowFlight(false);
			AOutput.send(player, "&f&lFLIGHT!&7 You have &cDisabled &7flight");
		} else {
			player.setAllowFlight(true);
			AOutput.send(player, "&f&lFLIGHT!&7 You have &aEnabled &7flight");
		}

		return false;
	}
}
