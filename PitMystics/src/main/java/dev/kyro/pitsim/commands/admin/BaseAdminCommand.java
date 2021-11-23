package dev.kyro.pitsim.commands.admin;

import dev.kyro.arcticapi.commands.ABaseCommand;
import dev.kyro.arcticapi.misc.AOutput;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class BaseAdminCommand extends ABaseCommand {
	public BaseAdminCommand(String executor) {
		super(executor);
	}

	@Override
	public void executeBase(CommandSender sender, List<String> args) {
		if(!sender.isOp() && sender instanceof Player) return;
		for(String line : createHelp().getMessage()) AOutput.sendIfPlayer(sender, line);
	}

	@Override
	public void executeFail(CommandSender sender, List<String> args) {
		executeBase(sender, args);
	}
}
