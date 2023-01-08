package dev.kyro.pitsim.commands;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.PlayerManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.misc.Lang;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class IgnoreCommand implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

		if(PlayerManager.isStaff(player.getUniqueId())) {
			AOutput.error(player, "&c&lERROR!&7 Staff are not allowed to ignore players");
			return false;
		}

		if(player.hasPermission("group.legendary")) {
			AOutput.error(player, "&c&lERROR!&7 You need to have &eLegendary &7rank to do that");
			return false;
		}

		if(args.length < 1) {
			AOutput.send(player, "&8&m--------------------&8<&c&lIGNORE&8>&m--------------------");
			AOutput.send(player, "&c * &7/" + label + " add <ign> &7(add a player to ignore list)");
			AOutput.send(player, "&c * &7/" + label + " remove <ign> &7(remove a player from ignore list)");
			AOutput.send(player, "&c * &7/" + label + " list &7(view your ignore list)");
			AOutput.send(player, "&8&m--------------------&8<&c&lIGNORE&8>&m--------------------");
			return false;
		}

		String command = args[0].toLowerCase();
		if(command.equals("add")) {
			if(args.length < 2) {
				AOutput.error(player, "&c&lERROR!&7 Usage: /" + label + " add <ign>");
				return false;
			}

			OfflinePlayer target;
			try {
				UUID uuid = UUID.fromString(args[1]);
				target = Bukkit.getOfflinePlayer(uuid);
			} catch(Exception ignored) {
				target = Bukkit.getOfflinePlayer(args[1]);
			}
			if(target == null) {
				Lang.COULD_NOT_FIND_PLAYER_WITH_NAME.send(player);
				return false;
			}

			if(pitPlayer.uuidIgnoreList.contains(target.getUniqueId().toString())) {
				AOutput.error(player, "&c&lERROR!&7 That player is already on your ignore list");
				return false;
			}

			if(target == player) {
				AOutput.error(player, "&c&lERROR!&7 You cannot ignore yourself");
				return false;
			}

			System.out.println("target: " + target.getName());
			if(PlayerManager.isStaff(target.getUniqueId())) {
				AOutput.error(player, "&c&lERROR!&7 You cannot ignore staff");
				return false;
			}

			pitPlayer.uuidIgnoreList.add(target.getUniqueId().toString());
			AOutput.send(player, "&c&lIGNORE!&7 You added " + target.getName() + " to your ignored list");
		} else if(command.equals("remove")) {
			if(args.length < 2) {
				AOutput.error(player, "&c&lERROR!&7 Usage: /" + label + " remove <ign>");
				return false;
			}

			OfflinePlayer target;
			try {
				UUID uuid = UUID.fromString(args[1]);
				target = Bukkit.getOfflinePlayer(uuid);
			} catch(Exception ignored) {
				target = Bukkit.getOfflinePlayer(args[1]);
			}
			if(target == null) {
				Lang.COULD_NOT_FIND_PLAYER_WITH_NAME.send(player);
				return false;
			}

			if(!pitPlayer.uuidIgnoreList.contains(target.getUniqueId().toString())) {
				AOutput.error(player, "&c&lERROR!&7 That player not on your ignore list");
				return false;
			}

			pitPlayer.uuidIgnoreList.remove(target.getUniqueId().toString());
			AOutput.send(player, "&c&lIGNORE!&7 You removed " + target.getName() + " from your ignored list");
		} else if(command.equals("list")) {

			if(pitPlayer.uuidIgnoreList.isEmpty()) {
				AOutput.send(player, "&c&lIGNORE!&7 Your ignore list is empty");
				return false;
			}

			AOutput.send(player, "&c&lIGNORE!&7You've ignored these players:");
			for(String playerUUIDString : pitPlayer.uuidIgnoreList) {
				OfflinePlayer ignoredPlayer = Bukkit.getOfflinePlayer(UUID.fromString(playerUUIDString));
				AOutput.send(player, "&c * &7" + ignoredPlayer.getName());
			}
		} else {
			AOutput.send(player, "&8&m--------------------&8<&c&lIGNORE&8>&m--------------------");
			AOutput.send(player, "&c * &7/" + label + " add <ign> &7(add a player to ignore list)");
			AOutput.send(player, "&c * &7/" + label + " remove <ign> &7(remove a player from ignore list)");
			AOutput.send(player, "&c * &7/" + label + " list &7(view your ignore list)");
			AOutput.send(player, "&8&m--------------------&8<&c&lIGNORE&8>&m--------------------");
		}

		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) return new ArrayList<>();
		Player player = (Player) sender;
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

		if(PlayerManager.isStaff(player.getUniqueId())) return new ArrayList<>();

		if(args.length < 2) return Misc.getTabComplete(args[0], "add", "remove", "list");
		String command = args[0].toLowerCase();
		if(command.equals("add")) {
			List<String> players = new ArrayList<>();
			for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
				if(onlinePlayer == player || PlayerManager.isStaff(onlinePlayer.getUniqueId())) continue;
				players.add(onlinePlayer.getName());
			}
			return Misc.getTabComplete(args[1], players);
		}
		if(command.equals("remove")) {
			List<String> ignoredPlayers = new ArrayList<>();
			for(String playerUUIDString : pitPlayer.uuidIgnoreList) {
				OfflinePlayer ignoredPlayer = Bukkit.getOfflinePlayer(UUID.fromString(playerUUIDString));
				ignoredPlayers.add(ignoredPlayer.getName());
			}
			return Misc.getTabComplete(args[1], ignoredPlayers);
		}
		return new ArrayList<>();
	}
}
