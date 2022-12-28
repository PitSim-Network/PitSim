package dev.kyro.pitsim.commands.admin;

import dev.kyro.arcticapi.commands.AMultiCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class BaseAdminCommand extends AMultiCommand {
	public BaseAdminCommand(String executor) {
		super(executor);
	}

	@Override
	public void execute(CommandSender sender, Command command, String alias, List<String> args) {
		if(!(sender instanceof Player)) return;
		Player player = (Player) sender;
		if(!player.isOp()) return;

		super.execute(sender, command, alias, args);
	}

	@Override
	public List<String> getTabComplete(Player player, String current, List<String> args) {
		if(!player.isOp()) return null;
		return null;
	}
}
