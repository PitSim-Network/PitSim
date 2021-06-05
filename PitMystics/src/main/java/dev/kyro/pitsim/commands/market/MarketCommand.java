package dev.kyro.pitsim.commands.market;

import dev.kyro.arcticapi.commands.ABaseCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class MarketCommand extends ABaseCommand {

	public MarketCommand(String executor) {
		super(executor);
	}

	@Override
	public void executeBase(CommandSender sender, List<String> args) {

		if(!(sender instanceof Player)) return;

		createHelp().send((Player) sender);
	}

	@Override
	public void executeFail(CommandSender sender, List<String> args) {

		executeBase(sender,args);
	}
}
