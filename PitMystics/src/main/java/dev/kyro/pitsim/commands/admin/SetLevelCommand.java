package dev.kyro.pitsim.commands.admin;

import dev.kyro.arcticapi.commands.ACommand;
import dev.kyro.arcticapi.commands.AMultiCommand;
import dev.kyro.arcticapi.data.APlayer;
import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.PrestigeValues;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.List;
import java.util.UUID;

public class SetLevelCommand extends ACommand {
	public SetLevelCommand(AMultiCommand base, String executor) {
		super(base, executor);
	}

	@Override
	public void execute(CommandSender sender, Command command, String alias, List<String> args) {
		if(!(sender instanceof Player)) return;
		Player player = (Player) sender;

		if(args.size() != 2) {
			AOutput.error(player, "&cCorrect usage: /ps set level <player> <level>");
		}

		File directory = new File("plugins/PitRemake/playerdata");
		File[] files = directory.listFiles();
		assert files != null;

		String targetPlayerString = args.get(0);

		for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			if(onlinePlayer.getName().equals(targetPlayerString)) {
				for(File file : files) {
					if(file.getName().equals(onlinePlayer.getUniqueId() + ".yml")) {
						onlinePlayer.kickPlayer(ChatColor.RED + "Your player data has changed. Please re-join.");
						resetData(args.get(1), player, file.getName());
						return;
					}
				}
				return;
			}
		}

		for(File file : files) {
			FileConfiguration data = YamlConfiguration.loadConfiguration(file);
			if(!data.contains("name")) continue;
			if(data.getString("name").equalsIgnoreCase(targetPlayerString)) {
				resetData(args.get(1), player, file.getName());
				return;
			}
		}
		AOutput.error(player, "&cUnable to find player!");
	}

	@Override
	public List<String> getTabComplete(Player player, String current, List<String> args) {
		return null;
	}

	public void resetData(String levelArg, Player player, String uuid) {
		new BukkitRunnable() {
			@Override
			public void run() {
				int level;
				try {
					level = Integer.parseInt(levelArg);
				} catch(Exception e) {
					AOutput.error(player, "&cInvalid number!");
					return;
				}

				if(level > 120 || level < 1) {
					AOutput.error(player, "&cInvalid number!");
					return;
				}

				UUID targetUUID = UUID.fromString(uuid.substring(0, uuid.length() - 4));
				APlayer aPlayer = APlayerData.getPlayerData(targetUUID);
				FileConfiguration playerData = aPlayer.playerData;

				playerData.set("level", level);
				PrestigeValues.PrestigeInfo info = PrestigeValues.getPrestigeInfo(playerData.getInt("prestige"));
				playerData.set("xp", (int) (PrestigeValues.getXPForLevel(level) * info.xpMultiplier));
				playerData.set("megastreak", "Overdrive");
				playerData.set("killstreak-0", "NoKillstreak");
				playerData.set("killstreak-1", "NoKillstreak");
				playerData.set("killstreak-2", "NoKillstreak");

				aPlayer.save();
				AOutput.send(player, "&aSuccess!");
			}
		}.runTaskLater(PitSim.INSTANCE, 10L);
	}
}