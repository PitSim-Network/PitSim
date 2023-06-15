package net.pitsim.pitsim.commands;

import dev.kyro.arcticapi.misc.AOutput;
import net.pitsim.pitsim.PitSim;
import net.pitsim.pitsim.controllers.SpawnManager;
import net.pitsim.pitsim.inventories.help.KitGUI;
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

		KitGUI kitGUI = new KitGUI(player);
		kitGUI.kitPanel.openPanel(kitGUI.kitPanel);

		return false;
	}
}
