package dev.kyro.pitsim.commands;

import dev.kyro.pitsim.inventories.PerkGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PerkCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;

		PerkGUI perkGUI = new PerkGUI(player);
		player.openInventory(perkGUI.getInventory());
		perkGUI.updateGUI();

		return false;
	}
}
