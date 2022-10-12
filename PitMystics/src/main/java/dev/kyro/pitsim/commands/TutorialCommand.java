package dev.kyro.pitsim.commands;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.tutorial.TutorialManager;
import dev.kyro.pitsim.tutorial.objects.Tutorial;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TutorialCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;

		if(args.length < 1) {
			AOutput.error(player, "&cUsage: /tutorial skip");
			return false;
		}

		if(!args[0].equalsIgnoreCase("skip")) {
			AOutput.error(player, "&cUsage: /tutorial skip");
			return false;
		}

		Tutorial tutorial = TutorialManager.getTutorial(player);
		if(tutorial == null) {
			AOutput.error(player, "&cYou are not in the tutorial!");
			return false;
		}

		tutorial.cleanUp();
		player.teleport(MapManager.currentMap.world.getSpawnLocation());
		PitPlayer.getPitPlayer(player).tutorial = true;
		AOutput.send(player, "&cYou have skipped the tutorial!");

		return false;
	}
}
