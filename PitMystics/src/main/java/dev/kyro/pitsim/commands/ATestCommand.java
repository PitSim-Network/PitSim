package dev.kyro.pitsim.commands;

import dev.kyro.pitsim.misc.Misc;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ATestCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;
		if(!player.isOp()) return false;

		DateFormat formatter = new SimpleDateFormat("dd MMM yyyy HH:mm:ss z");
		Date date = new Date();
		System.out.println("pre: " + formatter.format(date));
		System.out.println("post: " + formatter.format(Misc.convertToEST(date)));

		return false;
	}
}