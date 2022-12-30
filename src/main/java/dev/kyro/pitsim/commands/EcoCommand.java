package dev.kyro.pitsim.commands;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EcoCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;
		if(!player.isOp()) return false;

		if(args.length < 3) {
			AOutput.error(player, "&7Usage: /eco <give|take|set> <player> <amount>");
			return false;
		}

		String subCommand = args[0];
		if(subCommand.equalsIgnoreCase("give")) {
			Player target = Bukkit.getPlayer(args[1]);
			if(target == null) {
				AOutput.error(player, "&c&lERROR!&7 Could not find that player");
				return false;
			}

			double amount;
			try {
				amount = Double.parseDouble(args[2]);
				if(amount <= 0) throw new Exception();
			} catch(Exception ignored) {
				AOutput.error(player, "&c&lERROR!&7 Invalid amount");
				return false;
			}

			PitPlayer pitTarget = PitPlayer.getPitPlayer(target);
			pitTarget.gold += amount;
			AOutput.send(player, "&6&lECO!&7 Gave &6" + Misc.formatGoldFull(amount) + "g &7to " + Misc.getDisplayName(target));
			AOutput.send(target, "&6&lGOLD!&7 You received &6" + Misc.formatGoldFull(amount) + "g");
		} else if(subCommand.equalsIgnoreCase("take")) {
			Player target = Bukkit.getPlayer(args[1]);
			if(target == null) {
				AOutput.error(player, "&c&lERROR!&7 Could not find that player");
				return false;
			}

			double amount;
			try {
				amount = Double.parseDouble(args[2]);
				if(amount <= 0) throw new Exception();
			} catch(Exception ignored) {
				AOutput.error(player, "&c&lERROR!&7 Invalid amount");
				return false;
			}

			PitPlayer pitTarget = PitPlayer.getPitPlayer(target);
			if(amount > pitTarget.gold) {
				AOutput.error(player, "&c&lERROR!&7 That player only has &6" + Misc.formatGoldFull(pitTarget.gold) + "g");
				return false;
			}

			pitTarget.gold -= amount;
			AOutput.send(player, "&6&lECO!&7 Took &6" + Misc.formatGoldFull(amount) + "g &7from " + Misc.getDisplayName(target));
			AOutput.send(target, "&6&lGOLD!&6 " + Misc.formatGoldFull(amount) + "g &7was taken from you");
		} else if(subCommand.equalsIgnoreCase("set")) {
			Player target = Bukkit.getPlayer(args[1]);
			if(target == null) {
				AOutput.error(player, "&c&lERROR!&7 Could not find that player");
				return false;
			}

			double amount;
			try {
				amount = Double.parseDouble(args[2]);
				if(amount < 0) throw new Exception();
			} catch(Exception ignored) {
				AOutput.error(player, "&c&lERROR!&7 Invalid amount");
				return false;
			}

			PitPlayer pitTarget = PitPlayer.getPitPlayer(target);
			pitTarget.gold = amount;
			AOutput.send(player, "&6&lECO!&7 Set the balance of " + Misc.getDisplayName(player) + "&7 to &6" + Misc.formatGoldFull(amount) + "g");
			AOutput.send(target, "&6&lGOLD!&7 Your balance was set to &6" + Misc.formatGoldFull(amount) + "g");
		} else {
			AOutput.error(player, "&7Usage: /eco <give|take|set> <player> <amount>");
		}

		return false;
	}
}
