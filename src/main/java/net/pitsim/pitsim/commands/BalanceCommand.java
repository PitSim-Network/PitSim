package net.pitsim.pitsim.commands;

import dev.kyro.arcticapi.misc.AOutput;
import net.pitsim.pitsim.controllers.objects.PitPlayer;
import net.pitsim.pitsim.misc.Formatter;
import net.pitsim.pitsim.misc.Misc;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BalanceCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;

		if(!player.isOp() || args.length < 1) {
			PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
			AOutput.send(player, "&6&lGOLD!&7 You have " + Formatter.formatGoldFull(pitPlayer.gold));
			AOutput.send(player, "&f&lSOULS!&7 You have " + Formatter.formatSouls(pitPlayer.taintedSouls));
			return false;
		}

		Player target = Bukkit.getPlayer(args[0]);
		if(target == null) {
			AOutput.error(player, "&c&lERROR!&7 Could not find that player");
			return false;
		}

		PitPlayer pitTarget = PitPlayer.getPitPlayer(target);
		AOutput.send(player, "&6&lGOLD!&7 " + Misc.getDisplayName(target) + " &7has " + Formatter.formatGoldFull(pitTarget.gold));
		AOutput.send(player, "&f&lSOULS!&7 " + Misc.getDisplayName(target) + " &7has " + Formatter.formatSouls(pitTarget.taintedSouls));
		return false;
	}
}
