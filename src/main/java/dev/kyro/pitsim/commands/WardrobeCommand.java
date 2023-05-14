package dev.kyro.pitsim.commands;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.storage.EnderchestGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WardrobeCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;
		if(!player.isOp() && !PitSim.isDev()) return false;

		EnderchestGUI enderchestGUI = new EnderchestGUI(player, player.getUniqueId());
		enderchestGUI.enderchestPanel.openPanel(enderchestGUI.wardrobePanel);
		return false;
	}
}
