package net.pitsim.spigot.commands;

import net.pitsim.spigot.inventories.RenownShopGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RenownShopCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;
		if(!player.isOp()) return false;

		RenownShopGUI renownShopGUI = new RenownShopGUI(player);
		renownShopGUI.open();
		return false;
	}
}
