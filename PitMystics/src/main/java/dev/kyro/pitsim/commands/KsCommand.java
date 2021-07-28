package dev.kyro.pitsim.commands;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.controllers.LevelManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.killstreaks.Highlander;
import dev.kyro.pitsim.killstreaks.Uberstreak;
import dev.kyro.pitsim.misc.FunkyFeather;
import dev.kyro.pitsim.misc.ProtArmor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class KsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if(!(sender instanceof Player)) return false;
        Player player = (Player) sender;

//        FunkyFeather.giveFeather(player, 5);
//        AUtil.giveItemSafely(player, ProtArmor.getArmor("helmet"));
//        AUtil.giveItemSafely(player, ProtArmor.getArmor("chestplate"));
//        AUtil.giveItemSafely(player, ProtArmor.getArmor("leggings"));
//        AUtil.giveItemSafely(player, ProtArmor.getArmor("boots"));

//        PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
//
//        if(args.length < 1) return false;
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

        player.sendMessage(LevelManager.getXP(Integer.parseInt(args[0])) + " " + LevelManager.getPlayerKills(Integer.parseInt(args[0])));
        return false;
    }
}
