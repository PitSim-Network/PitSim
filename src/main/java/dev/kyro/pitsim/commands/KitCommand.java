package dev.kyro.pitsim.commands;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.SpawnManager;
import dev.kyro.pitsim.inventories.help.HelpGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KitCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;

		if(!player.isOp() && !PitSim.isDev()) {
			if(!SpawnManager.isInSpawn(player)) {
				AOutput.error(player, "You can only use this command in spawn!");
				return false;
			}
		}

		HelpGUI helpGUI = new HelpGUI(player);
		helpGUI.kitPanel.openPanel(helpGUI.kitPanel);

		return false;
	}
}
