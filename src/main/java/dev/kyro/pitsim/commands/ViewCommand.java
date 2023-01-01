package dev.kyro.pitsim.commands;

import de.myzelyam.api.vanish.VanishAPI;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.inventories.view.ViewGUI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ViewCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;

		if(args.length < 1) {
			AOutput.error(player, "Usage: /view <player>");
			return false;
		}

		Player target = null;
		for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			if(!onlinePlayer.getName().equalsIgnoreCase(args[0])) continue;
			if(VanishAPI.isInvisible(onlinePlayer)) continue;
			target = onlinePlayer;
			break;
		}
		if(target == null) {
			AOutput.error(player, "&c&lERROR! &7Could not find that player");
			return false;
		}

		new ViewGUI(player, target).open();
		return false;
	}
}