package net.pitsim.pitsim.commands;

import net.pitsim.pitsim.battlepass.inventories.PassGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class QuestsCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;

		PassGUI passGUI = new PassGUI(player);
		passGUI.passPanel.openPanel(passGUI.questPanel);

		return false;
	}
}
