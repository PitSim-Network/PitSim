package dev.kyro.pitsim.commands;

import dev.kyro.pitsim.controllers.HopperManager;
import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.objects.Hopper;
import dev.kyro.pitsim.helmetabilities.PhoenixAbility;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        Player player = (Player) sender;
        player.teleport(MapManager.getPlayerSpawn());
        PhoenixAbility.alreadyActivatedList.remove(player.getUniqueId());
        for(Hopper hopper : HopperManager.hopperList) {
            if(player != hopper.target) continue;
            hopper.remove();
        }

        return false;
    }
}
