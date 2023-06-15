package net.pitsim.spigot.commands;

import net.pitsim.spigot.battlepass.inventories.PassGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PassCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;

		PassGUI passGUI = new PassGUI(player);
		passGUI.open();

		return false;
	}
}
