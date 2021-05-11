package dev.kyro.pitremake.commands;

import dev.kyro.pitremake.nons.Non;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NonCommand implements CommandExecutor {

	public static Player target;

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;

		target = player;
		new Non(ATestCommand.hoppers.get((int) (Math.random() * ATestCommand.hoppers.size())));
//		new Non("bluetango766");

		return false;
	}
}