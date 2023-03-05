package dev.kyro.pitsim.commands;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ATestCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;
		if(!player.isOp()) return false;

		String message = "&7Reality destabilizes. Will it make you stronger or will " +
				"you succumb to the endless void of time";
		AOutput.send(player, "&e&lRNGESUS!&7 " + Misc.distortMessage(message, 0.15));

		return false;
	}
}