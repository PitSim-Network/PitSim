package dev.kyro.pitsim.commands;

import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.inventories.RenownShopGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if(!(sender instanceof Player)) return false;
        Player player = (Player) sender;

        if(!player.isOp()) return false;

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

//        player.sendMessage(LevelManager.getXP(Integer.parseInt(args[0])) + " " + LevelManager.getPlayerKills(Integer.parseInt(args[0])));

//        Bukkit.broadcastMessage(CaptureTheFlag.respawningPlayers.toString());

        RenownShopGUI renownShopGUI = new RenownShopGUI(player);
        renownShopGUI.open();

        if(args.length > 0) {
            PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
            if(args[0].equalsIgnoreCase("renown")) pitPlayer.renown += 50;
        }

        return false;
    }
}
