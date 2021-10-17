package dev.kyro.pitsim.commands;

import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.LevelManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class SetLevelCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if(!(sender instanceof Player)) return false;
        Player player = (Player) sender;

        PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

        try {
            pitPlayer.level = Integer.parseInt(args[0]) - 1;
            pitPlayer.remainingXP = 0;
            LevelManager.incrementLevel(player);
        } catch(Exception e) {
            AOutput.error(player, "&cLevel set Failed!");
            return false;
        }
        FileConfiguration playerData = APlayerData.getPlayerData(player);
        playerData.set("level", pitPlayer.level);
        APlayerData.savePlayerData(player);
        AOutput.send(player, "&aSuccess!");



       return false;
    }
}
