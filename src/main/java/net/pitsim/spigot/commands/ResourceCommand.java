package net.pitsim.spigot.commands;

import dev.kyro.arcticapi.misc.AOutput;
import net.pitsim.spigot.controllers.objects.PitPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ResourceCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;

		if(args.length > 0) {
			if(args[0].equals("toggle")) {
				PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
				if(pitPlayer.promptPack) {
					pitPlayer.promptPack = false;
					AOutput.send(player, "&cYou will no longer automatically receive the resource pack on join.");
				} else {
					pitPlayer.promptPack = true;
					AOutput.send(player, "&aYou will now automatically receive the resource pack on join.");
				}
			} else {
				AOutput.error(player, "&cCorrect usage: /resource toggle");
			}
		} else {
//			ube
//			player.setResourcePack("https://cdn.discordapp.com/attachments/803483152630677524/903075400442314772/PitSim.zip");
//			neverglitch
			player.setResourcePack("https://cdn.discordapp.com/attachments/801905085130211388/1038659555791474718/Nebula_PitEdit.zip");
		}

		return false;
	}
}
