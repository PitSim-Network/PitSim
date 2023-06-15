package net.pitsim.pitsim.commands.essentials;

import dev.kyro.arcticapi.misc.AOutput;
import net.pitsim.pitsim.misc.Misc;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BroadcastCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission("pitsim.broadcast")) return false;

		if(args.length < 1) {
			AOutput.error(sender, "&c&lERROR!&7 Usage: /" + label + " <message>");
			return false;
		}

		Misc.broadcast(String.join(" ", args));
		return false;
	}
}
