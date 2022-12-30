package dev.kyro.pitsim.commands.essentials;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.objects.PluginMessage;
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

		new PluginMessage()
				.writeString("BROADCAST")
				.writeString(String.join(" ", args))
				.send();

		return false;
	}
}
