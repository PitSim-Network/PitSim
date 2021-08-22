package dev.kyro.pitsim.commands;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.AFKManager;
import dev.kyro.pitsim.controllers.MapManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AFKCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        Player player = (Player) sender;
        if(AFKManager.AFKPlayers.contains(player)) {
            AFKManager.AFKPlayers.remove(player);
            AOutput.send(player, "&cYou are no longer AFK!");
        }
        else {
            AFKManager.AFKPlayers.add(player);
            AFKManager.AFKRotations.remove(player);
            AOutput.send(player, "&cYou are now AFK!");
        }

        AFKManager.onlineActivePlayers = 0;
        for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if(!AFKManager.AFKPlayers.contains(onlinePlayer)) AFKManager.onlineActivePlayers++;
        }

        return false;
    }
}
