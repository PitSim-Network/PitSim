package dev.kyro.pitsim.commands;

import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class SetPrestigeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if(!(sender instanceof Player)) return false;
        Player player = (Player) sender;

        PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

        try {
            pitPlayer.prestige = Integer.parseInt(args[0]);
        } catch(Exception e) {
            AOutput.error(player, "&cPrestige set Failed!");
            return false;
        }
        FileConfiguration playerData = APlayerData.getPlayerData(player);
        playerData.set("prestige", pitPlayer.prestige);

        pitPlayer.moonBonus = 0;
        playerData.set("moonbonus", pitPlayer.moonBonus);

        pitPlayer.goldStack = 0;
        playerData.set("goldstack", pitPlayer.goldStack);

        APlayerData.savePlayerData(player);
        AOutput.send(player, "&aSuccess!");



       return false;
    }
}
