package net.pitsim.pitsim.commands;

import dev.kyro.arcticapi.misc.AOutput;
import net.pitsim.pitsim.controllers.ShutdownManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ShutdownCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if(!sender.isOp()) return false;

		if(args.length < 1) {
			AOutput.send(sender, "&cUsage: /ps shutdown <minutes>");
			return false;
		}

		int minutes;
		try {
			minutes = Integer.parseInt(args[0]);
		} catch(Exception e) {
			AOutput.send(sender, "&cInvalid Parameters. Usage: /ps shutdown <minutes>");
			return false;
		}

		if(minutes < 1) {
			AOutput.send(sender, "&cInvalid Parameters. Usage: /ps shutdown <minutes>");
			return false;
		}

		if(ShutdownManager.isShuttingDown) {
			AOutput.send(sender, "&cThe server is already shutting down!");
			return false;
		}

		if(minutes != 0) {
			ShutdownManager.initiateShutdown(minutes);
			AOutput.send(sender, "&aShutdown Initiated!");
		}
		return false;
	}
}
