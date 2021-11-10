package dev.kyro.pitsim.commands;

import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.arcticapi.misc.AOutput;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class LightningCommand implements CommandExecutor {
	public static List<Player> lightningPlayers = new ArrayList<>();

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;

        FileConfiguration playerData = APlayerData.getPlayerData(player);
        boolean lightningDisabled = playerData.getBoolean("settings.lightning");
        playerData.set("settings.lightning", !lightningDisabled);
        APlayerData.savePlayerData(player);

		if(lightningDisabled) {
			AOutput.send(player, "Lightning enabled");
			lightningPlayers.remove(player);
			return false;
		} else {
			AOutput.send(player, "Lightning disabled");
			lightningPlayers.add(player);
			return false;
		}
	}
}
