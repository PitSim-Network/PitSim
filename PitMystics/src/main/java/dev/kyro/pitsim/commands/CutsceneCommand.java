package dev.kyro.pitsim.commands;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.CutsceneManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CutsceneCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;

		if(args.length < 1) {
			AOutput.error(player, "&cUsage: /cutscene skip");
			return false;
		}

		if(!args[0].equalsIgnoreCase("skip")) {
			AOutput.error(player, "&cUsage: /cutscene skip");
			return false;
		}

		if(!CutsceneManager.cutscenePlayers.containsKey(player)) {
			AOutput.error(player, "&cYou are not in the tutorial!");
			return false;
		}

		CutsceneManager.skip(player);
		AOutput.send(player, "&cYou have skipped the Cutscene!");

		return false;
	}
}
