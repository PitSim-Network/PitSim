package net.pitsim.pitsim.commands.essentials;

import net.pitsim.pitsim.inventories.trash.TrashGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TrashCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;

		new TrashGUI(player).open();
		return false;
	}
}
