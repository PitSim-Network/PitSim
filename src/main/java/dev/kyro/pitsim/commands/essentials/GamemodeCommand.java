package dev.kyro.pitsim.commands.essentials;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GamemodeCommand implements CommandExecutor {
	public static GamemodeCommand INSTANCE;

	public GamemodeCommand() {
		INSTANCE = this;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;
		if(!player.hasPermission("pitsim.gamemode")) return false;

		GamemodeInfo gamemodeInfo = null;
		String targetName = null;
		if(label.equals("gm") || label.equals("gamemode")) {
			if(args.length < 1) {
				AOutput.error(player, "&c&lERROR!&7 Please specify a gamemode");
				return false;
			}

			gamemodeInfo = GamemodeInfo.getGamemode(args[0]);
			if(gamemodeInfo == null) {
				AOutput.error(player, "&c&lERROR!&7 Could not find that gamemode");
				return false;
			}
			if(args.length >= 2) targetName = args[1];
		} else {
			if(args.length >= 1) targetName = args[0];
		}

		if(label.equals("gms")) {
			gamemodeInfo = GamemodeInfo.SURVIVAL;
		} else if(label.equals("gmc")) {
			gamemodeInfo = GamemodeInfo.CREATIVE;
		} else if(label.equals("gma")) {
			gamemodeInfo = GamemodeInfo.ADVENTURE;
		} else if(label.equals("gmsp")) {
			gamemodeInfo = GamemodeInfo.SPECTATOR;
		}
		assert gamemodeInfo != null;

		Player target;
		if(targetName != null) {
			target = Bukkit.getPlayer(targetName);
			if(target == null) {
				AOutput.error(player, "&c&lERROR!&7 could not find that player");
				return false;
			}
		} else {
			target = player;
		}

		target.setGameMode(gamemodeInfo.gameMode);
		AOutput.send(target, "&a&lGAMEMODE!&7 Switched gamemode to " + gamemodeInfo.displayName);
		if(target != player) AOutput.send(player, "&a&lGAMEMODE!&7 Switched gamemode of " +
				Misc.getDisplayName(target) + " &7to " + gamemodeInfo.displayName);

		return false;
	}

	public enum GamemodeInfo {
		SURVIVAL(GameMode.SURVIVAL, "&cSurvival", "survival", "0", "s"),
		CREATIVE(GameMode.CREATIVE, "&9Creative", "creative", "1", "c"),
		ADVENTURE(GameMode.ADVENTURE, "&eAdventure", "adventure", "2", "a"),
		SPECTATOR(GameMode.SPECTATOR, "&fSpectator", "spectator", "3", "sp");

		public GameMode gameMode;
		public String displayName;
		public List<String> refNames;

		GamemodeInfo(GameMode gameMode, String displayName, String... refNames) {
			this.gameMode = gameMode;
			this.displayName = displayName;
			this.refNames = new ArrayList<>(Arrays.asList(refNames));
		}

		public static GamemodeInfo getGamemode(String refName) {
			for(GamemodeInfo value : values()) if(value.refNames.contains(refName.toLowerCase())) return value;
			return null;
		}
	}
}
