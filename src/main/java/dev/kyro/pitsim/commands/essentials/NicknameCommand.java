package dev.kyro.pitsim.commands.essentials;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.misc.Lang;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NicknameCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;
		Player target;
		String nickname;

		if(player.isOp() && args.length >= 2) {
			target = Bukkit.getPlayer(args[0]);
			if(target == null) {
				Lang.COULD_NOT_FIND_PLAYER_WITH_NAME.send(player);
				return false;
			}
			if(!target.hasPermission("pitsim.nick")) {
				AOutput.error(player, "&c&lERROR!&7 They need to have &4Eternal &7to be able to have a nickname");
				return false;
			}
			nickname = args[1];
		} else {
			if(!player.hasPermission("pitsim.nick")) {
				AOutput.error(player, "&c&lERROR!&7 You need to have &4Eternal &7to be able to nick");
				return false;
			}
			if(args.length < 1) {
				if(player.isOp()) {
					AOutput.error(player, "&c&lERROR!&7 Usage: /" + label + " [player] <nickname|reset>");
				} else {
					AOutput.error(player, "&c&lERROR!&7 Usage: /" + label + " <nickname|reset>");
				}
				return false;
			}
			target = player;
			nickname = args[0];
		}

		PitPlayer pitTarget = PitPlayer.getPitPlayer(target);
		if(nickname.equalsIgnoreCase("off") || nickname.equalsIgnoreCase("reset")) {
			pitTarget.nickname = null;
			AOutput.send(target, "&4&lNICK!&7 Turned off your nickname");
			if(target != player) AOutput.send(player, "&4&lNICK!&7 Turned off " + Misc.getDisplayName(target) + "'s &7nickname");
			return false;
		}

		if(!player.isOp() && !nickname.matches("^\\w+$")) {
			AOutput.error(player, "&c&lERROR!&7 You can only have regular characters in a nickname");
			return false;
		}
		if(nickname.length() > 16) {
			AOutput.error(player, "&c&lERROR!&7 Nicknames can only be up to 16 characters long");
			return false;
		}
		OfflinePlayer nickTest = Bukkit.getOfflinePlayer(nickname);
//		TODO: Check against the proxy instead of the spigot server
		if(!player.isOp() && nickTest.hasPlayedBefore()) {
			AOutput.error(player, "&c&lERROR!&7 A player has already logged in with that name");
			return false;
		}

		pitTarget.nickname = nickname;
		AOutput.send(player, "&4&lNICK!&7 Your nickname was set to " + Misc.getDisplayName(player));
		if(target != player) AOutput.send(player, "&4&lNICK!&7 Set " + Misc.getDisplayName(target) + "'s &7nickname");
		return false;
	}
}
