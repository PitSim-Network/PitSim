package dev.kyro.pitsim.commands;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.killstreaks.Highlander;
import dev.kyro.pitsim.killstreaks.Uberstreak;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if(!(sender instanceof Player)) return false;
        Player player = (Player) sender;

        PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

        if(args.length < 1) return false;
//
//        if(args[0].equalsIgnoreCase("Highlander")) {
//            pitPlayer.megastreak = new Highlander(pitPlayer);
//            AOutput.send(player, "&aSuccessfully equipped &6Highlander&a!");
//        }
//
//        if(args[0].equalsIgnoreCase("Uberstreak")) {
//            pitPlayer.megastreak = new Uberstreak(pitPlayer);
//            AOutput.send(player, "&aSuccessfully equipped &dUberstreak&a!");
//        }
        return false;
    }
}
