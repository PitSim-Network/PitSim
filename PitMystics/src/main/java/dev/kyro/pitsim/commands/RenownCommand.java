package dev.kyro.pitsim.commands;

import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.inventories.RenownShopGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RenownCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if(!(sender instanceof Player)) return false;
        Player player = (Player) sender;
        if(!player.isOp())  return false;


        RenownShopGUI renownShopGUI = new RenownShopGUI(player);
        renownShopGUI.open();

        if(args.length > 0 && player.isOp()) {
            PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
            if(args[0].equalsIgnoreCase("renown")) pitPlayer.renown += 50;
        }

        return false;
    }
}
