package dev.kyro.pitsim.commands.admin;

import dev.kyro.arcticapi.commands.ASubCommand;
import dev.kyro.pitsim.controllers.PlayerManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class BypassCommand extends ASubCommand {
	public BypassCommand(String executor) {
		super(executor);
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if(!sender.isOp()) return;
		Player player = (Player) sender;
		if(PlayerManager.toggledPlayers.contains(player)) PlayerManager.toggledPlayers.remove(player);
		else PlayerManager.toggledPlayers.add(player);

	}

}
