package dev.kyro.pitsim.commands.admin;

import dev.kyro.arcticapi.commands.ACommand;
import dev.kyro.arcticapi.commands.AMultiCommand;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.ahelp.HelpManager;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class KyroCommand extends ACommand {
	public KyroCommand(AMultiCommand base, String executor) {
		super(base, executor);
	}

	@Override
	public void execute(CommandSender sender, Command command, String alias, List<String> args) {
		if(!(sender instanceof Player)) return;
		Player player = (Player) sender;
		if(!Misc.isKyro(player.getUniqueId())) {
			AOutput.error(player, "&c&lERROR!&7 You have to be &9Kyro &7to do this");
			return;
		}

		if(args.isEmpty()) {
			AOutput.error(player, "&c&lERROR!&7 Usage: <sync|clear>");
			return;
		}

		String subCommand = args.get(0).toLowerCase();
		if(subCommand.equals("sync")) {
			AOutput.send(player, "&9&lAI!&7 Updating Dialogflow model");
			new Thread(HelpManager::updateIntentsAndPages).start();
		} else if(subCommand.equals("clear")) {
			AOutput.send(player, "&9&lAI!&7 Clearing saved Dialogflow intent requests");
			HelpManager.clearStoredData();
		}
	}

	@Override
	public List<String> getTabComplete(Player player, String current, List<String> args) {
		return null;
	}
}
