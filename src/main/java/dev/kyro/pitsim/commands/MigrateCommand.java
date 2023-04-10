package dev.kyro.pitsim.commands;

import dev.kyro.pitsim.controllers.MigrationManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MigrateCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;
		if(!player.isOp()) return false;

		MigrationManager.migrateAltar();


//		if(args.length > 0) {
//			PitPlayer pitPlayer = new PitPlayer(Bukkit.getOfflinePlayer(args[0]).getUniqueId());
//			pitPlayer.save(false, false);
//			return false;
//		}

		return false;
	}
}