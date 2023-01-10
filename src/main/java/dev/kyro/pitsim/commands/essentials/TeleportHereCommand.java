package dev.kyro.pitsim.commands.essentials;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.misc.Lang;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportHereCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;
		if(!player.hasPermission("pitsim.teleport")) {
			Lang.NO_PERMISSION.send(player);
			return false;
		}

		if(args.length < 1) {
			AOutput.error(player, "&c&lERROR!&7 Usage: /" + label + " <target>");
			return false;
		}

		Player target = Bukkit.getPlayer(args[0]);
		if(target == null) {
			AOutput.error(player, "&c&lERROR!&7 Could not find a player with the name: " + args[0]);
			return false;
		}

		if(target == player) {
			AOutput.error(player, "&c&lERROR!&7 You cannot teleport to yourself");
			return false;
		}

		TeleportCommand.TPLocation tpLocation = new TeleportCommand.TPLocation(player);
		tpLocation.teleport(target);
		tpLocation.sendThirdPartyTeleportMessage(target, player);
		return false;
	}
}
