package dev.kyro.pitsim.commands.admin;

import dev.kyro.arcticapi.commands.ACommand;
import dev.kyro.arcticapi.commands.AMultiCommand;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.PlayerManager;
import dev.kyro.pitsim.controllers.ShutdownManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ExtendCommand extends ACommand {
	public ExtendCommand(AMultiCommand base, String executor) {
		super(base, executor);
	}

	@Override
	public void execute(CommandSender sender, Command command, String alias, List<String> args) {
		if(!sender.isOp()) return;

		if(!(sender instanceof Player)) return;
		Player player = (Player) sender;

		if(args.size() != 1) {
			AOutput.send(player, "&cInvalid arguments! /extend <minutes>");
		}

		int minutes = 0;

		try {
			minutes = Integer.parseInt(args.get(0));
		} catch(Exception e) {
			AOutput.send(player, "&cInvalid arguments! /extend <minutes>");
			return;
		}

		if(minutes < 1) {
			AOutput.send(player, "&cInvalid arguments! /extend <minutes>");
			return;
		}

		if(ShutdownManager.minutes == 0 && ShutdownManager.seconds < 10) {
			AOutput.send(player, "&cIt is too late to extend the shutdown!");
			return;
		}

		ShutdownManager.minutes += minutes;

		AOutput.send(player, "&aShutdown extended by " + minutes + " minutes!");
	}

	@Override
	public List<String> getTabComplete(Player player, String current, List<String> args) {
		return null;
	}
}
