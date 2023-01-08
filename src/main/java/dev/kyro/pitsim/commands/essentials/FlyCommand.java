package dev.kyro.pitsim.commands.essentials;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.misc.Lang;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class FlyCommand implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;
		if(!player.hasPermission("pitsim.flight")) {
			Lang.NO_PERMISSION.send(player);
			return false;
		}

		Player target = player;
		if(args.length > 0) {
			target = Bukkit.getPlayer(args[0]);
		}

		if(target == null) {
			Lang.COULD_NOT_FIND_PLAYER_WITH_NAME.send(player);
			return false;
		}

		if(target.getAllowFlight()) {
			target.setAllowFlight(false);
			AOutput.send(target, "&f&lFLIGHT!&7 Your flight has been &cDisabled");
			if(target != player) AOutput.send(player, "&f&lFLIGHT!&7 You have &cDisabled &7flight for " + Misc.getDisplayName(target));
		} else {
			target.setAllowFlight(true);
			AOutput.send(target, "&f&lFLIGHT!&7 Your flight has been &aEnabled");
			if(target != player) AOutput.send(player, "&f&lFLIGHT!&7 You have &aEnabled &7flight for " + Misc.getDisplayName(target));
		}

		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) return new ArrayList<>();
		Player player = (Player) sender;
		if(!player.hasPermission("pitsim.flight")) return new ArrayList<>();

		List<String> players = new ArrayList<>();
		for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			if(onlinePlayer == player) continue;
			players.add(onlinePlayer.getName());
		}
		return Misc.getTabComplete(args[0], players);
	}
}
