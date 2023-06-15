package net.pitsim.pitsim.commands;

import net.pitsim.pitsim.controllers.NonManager;
import net.pitsim.pitsim.controllers.objects.Non;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NonCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;

		if(!player.isOp()) return false;

		new Non(NonManager.botSkins.get((int) (Math.random() * NonManager.botSkins.size())));

		return false;
	}
}