package net.pitsim.spigot.commands;

import dev.kyro.arcticapi.misc.AOutput;
import net.pitsim.spigot.controllers.BoosterManager;
import net.pitsim.spigot.controllers.ProxyMessaging;
import net.pitsim.spigot.controllers.objects.Booster;
import net.pitsim.spigot.inventories.BoosterGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BoosterCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;

		if(player.isOp() && args.length != 0) {

			String concatenatedBoosters = "";
			for(Booster booster : BoosterManager.boosterList)
				concatenatedBoosters += concatenatedBoosters.isEmpty() ? booster.refName : ", " + booster.refName;
			if(args.length < 2) {
				AOutput.send(player, "Usage: /booster <" + concatenatedBoosters + "> <minutes|clear>");
				return false;
			}

			Booster booster = BoosterManager.getBooster(args[0]);
			if(booster == null) {
				AOutput.error(player, "That booster does not exist");
				return false;
			}

			int minutes;
			try {
				minutes = Integer.parseInt(args[1]);
			} catch(NumberFormatException ignored) {
				if(!args[1].equalsIgnoreCase("clear")) {
					AOutput.error(player, "That is not a valid time");
					return false;
				}

				booster.disable();
				return false;
			}

			AOutput.send(player, "&7Added &b" + minutes + " &7minutes to the booster. The booster will be active for &b" + (booster.minutes + minutes) + " &7more minute" +
					(booster.minutes == 1 ? "" : "s"));

			ProxyMessaging.sendBoosterUse(booster, player, minutes, false);

			return false;
		} else {
			BoosterGUI boosterGUI = new BoosterGUI(player);
			boosterGUI.open();
			return false;
		}
	}
}
