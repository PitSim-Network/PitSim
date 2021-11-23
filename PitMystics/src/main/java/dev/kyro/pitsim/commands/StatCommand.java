package dev.kyro.pitsim.commands;

import dev.kyro.pitsim.inventories.StatGUI;
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
