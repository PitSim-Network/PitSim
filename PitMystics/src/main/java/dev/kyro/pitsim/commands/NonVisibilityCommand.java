package dev.kyro.pitsim.commands;

import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.NonManager;
import dev.kyro.pitsim.controllers.objects.Non;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class NonVisibilityCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if(!(sender instanceof Player)) return false;
        Player player = (Player) sender;
//        if(!player.hasPermission("pitsim.bounty")) return false;

        FileConfiguration playerData = APlayerData.getPlayerData(player);
        boolean nonsHidden = playerData.getBoolean("misc.nons-hidden");
        playerData.set("misc.nons-hidden", !nonsHidden);

        if(nonsHidden) {
            for(Non non : NonManager.nons) player.showPlayer(non.non);
            AOutput.send(player, "Nons now visible");
            return false;
        } else {
            for(Non non : NonManager.nons) player.hidePlayer(non.non);
            AOutput.send(player, "Nons now hidden");
            return false;
        }
    }
}
