package net.pitsim.pitsim.commands;

import dev.kyro.arcticapi.misc.AOutput;
import net.pitsim.pitsim.controllers.ChatTriggerManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ChatTriggerDataCommand implements CommandExecutor {
	public static List<SubCommand> subCommands = new ArrayList<>();

	public ChatTriggerDataCommand() {
		registerSubCommand("status", context -> {
			Player player = context.getPlayer();
			if(ChatTriggerManager.isSubscribed(player)) {
				AOutput.send(player, ChatTriggerManager.PREFIX + "Status: &a&lSUBSCRIBED");
			} else {
				AOutput.send(player, ChatTriggerManager.PREFIX + "Status: &c&lNOT SUBSCRIBED");
			}
		});

		registerSubCommand("alldata", context -> {
			Player player = context.getPlayer();
			if(!ChatTriggerManager.isSubscribed(player)) {
				AOutput.send(player, ChatTriggerManager.PREFIX + "You must be subscribed to receive data");
				return;
			}
			ChatTriggerManager.sendAllData(player);
		});
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;

		if(args.length == 0) {
			AOutput.send(player, "&1&m-------------------&1<&9&lDATA&1>&m-------------------");
			for(SubCommand subCommand : subCommands) AOutput.send(player, "&1 * &9/" + label + " " + subCommand.getCommand());
			AOutput.send(player, "&1&m-------------------&1<&9&lDATA&1>&m-------------------");
			return false;
		}

		for(SubCommand subCommand : subCommands) {
			if(!subCommand.command.equalsIgnoreCase(args[0])) continue;
			subCommand.getExecution().accept(new CommandContext(sender, cmd, label, args));
			return false;
		}

		return false;
	}

	public static void registerSubCommand(String command, Consumer<CommandContext> execution) {
		subCommands.add(new SubCommand(command, execution));
	}

	public static class SubCommand {
		private final String command;
		private final Consumer<CommandContext> execution;

		public SubCommand(String command, Consumer<CommandContext> execution) {
			this.command = command;
			this.execution = execution;
		}

		public String getCommand() {
			return command;
		}

		public Consumer<CommandContext> getExecution() {
			return execution;
		}
	}

	public static class CommandContext {
		private final Player player;
		private final Command cmd;
		private final String label;
		private final String[] args;

		public CommandContext(CommandSender sender, Command cmd, String label, String[] args) {
			this.player = (Player) sender;
			this.cmd = cmd;
			this.label = label;
			this.args = args;
		}

		public Player getPlayer() {
			return player;
		}

		public Command getCmd() {
			return cmd;
		}

		public String getLabel() {
			return label;
		}

		public String[] getArgs() {
			return args;
		}
	}
}
