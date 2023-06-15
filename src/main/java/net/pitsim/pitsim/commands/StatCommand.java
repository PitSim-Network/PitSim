package net.pitsim.pitsim.commands;

import net.pitsim.pitsim.inventories.stats.StatGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;
		if(!player.isOp()) return false;

		StatGUI statGUI = new StatGUI(player);
		statGUI.open();
		return false;
	}
}
