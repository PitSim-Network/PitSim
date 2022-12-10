package dev.kyro.pitsim.commands;

import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MigrateCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if(args.length > 0) {
			PitPlayer pitPlayer = new PitPlayer(Bukkit.getOfflinePlayer(args[0]).getUniqueId());
			pitPlayer.save(true);
			return false;
		}

		return false;
	}
}