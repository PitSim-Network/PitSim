package dev.kyro.pitsim.commands.beta;

import dev.kyro.arcticapi.commands.AMultiCommand;
import dev.kyro.pitsim.PitSim;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class BaseBetaCommand extends AMultiCommand {
	public BaseBetaCommand(String executor) {
		super(executor);
	}

	@Override
	public void execute(CommandSender sender, Command command, String alias, List<String> args) {
		if(!(sender instanceof Player)) return;
		Player player = (Player) sender;
		if(!PitSim.isDev() && !player.isOp()) return;

		super.execute(sender, command, alias, args);
	}

	@Override
	public List<String> getTabComplete(Player player, String current, List<String> args) {
		if(!player.isOp()) return null;
		return null;
	}
}
