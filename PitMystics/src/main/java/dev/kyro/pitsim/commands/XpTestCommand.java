package dev.kyro.pitsim.commands;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.ExperienceManager;
import dev.kyro.pitsim.controllers.LeaderboardManager;
import dev.kyro.pitsim.controllers.LevelManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class XpTestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        Player player = (Player) sender;

//        AOutput.send(player, String.valueOf(LevelManager.getXP(Integer.parseInt(args[0]))));

//        AOutput.send(player, String.valueOf(Math.round(LevelManager.getXP(Integer.parseInt(args[0])) / 20)));


//        AOutput.send(player, LeaderboardManager.finalSorted.toString());


        String key = (String) LeaderboardManager.finalSorted.keySet().toArray()[0];
        int value = LeaderboardManager.finalLevels.get(key);


        AOutput.send(player, key);
        AOutput.send(player, String.valueOf(value));
//        AOutput.send(player, String.valueOf(value));


        return false;
    }


}
