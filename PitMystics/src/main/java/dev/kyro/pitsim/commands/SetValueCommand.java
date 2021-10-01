package dev.kyro.pitsim.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetValueCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = (Player) sender;
        if(!player.isOp()) return false;
//
//        if(args.length < 4) {
//            AOutput.error(player, "&cInvalid usage: /setvalue <player> <stat> <type> <value>");
//        }
//
//
//        String playerString = args[0];
//        String stat = args[1];
//        String type = args[2];
//        String value = args[3];
//        Player targetPlayer = null;
//
//        for(Player players : Bukkit.getOnlinePlayers()) {
//            if(players.getName().equalsIgnoreCase(playerString)) {
//                targetPlayer = players;
//            }
//        }
//
//        if(targetPlayer == null) {
//            AOutput.error(player, "&cInvalid player");
//            return false;
//        }
//
//        FileConfiguration playerData = APlayerData.getPlayerData(targetPlayer);
//
//        if(type.equalsIgnoreCase("boolean")) {
//            if(!value.equalsIgnoreCase("true") && !value.equalsIgnoreCase("false")) {
//                AOutput.error(player, "&cInvalid data value for type boolean. (true, false)");
//                return false;
//            }
//            if(value.equalsIgnoreCase("true")) playerData.set(stat, true);
//            if(value.equalsIgnoreCase("false")) playerData.set(stat, false);
//            AOutput.send(player, "&7Set value &f" + stat + " &7for player &f" + targetPlayer.getName() + " &7to &f" + value.toLowerCase(Locale.ROOT));
//        } else if(type.equalsIgnoreCase("string")) {
//            playerData.set(stat, value);
//            AOutput.send(player, "&7Set value &f" + stat + " &7for player &f" + targetPlayer.getName() + " &7to &f" + value.toLowerCase(Locale.ROOT));
//        } else if(type.equalsIgnoreCase("int") || type.equalsIgnoreCase("integer"))  {
//            int num = 0;
//            try {
//                num = Integer.parseInt(value);
//            } catch(Exception e) {
//                AOutput.error(player, "&cInvalid data value for type int. (Any valid number)");
//                return false;
//            }
//            playerData.set(stat, num);
//            AOutput.send(player, "&7Set value &f" + stat + " &7for player &f" + targetPlayer.getName() + " &7to &f" + value.toLowerCase(Locale.ROOT));
//        } else if(type.equalsIgnoreCase("double")) {
//            double num = 0;
//            try {
//                num = Double.parseDouble(value);
//            } catch(Exception e) {
//                AOutput.error(player, "&cInvalid data value for type double. (Any valid number wih decimal)");
//                return false;
//            }
//            playerData.set(stat, num);
//            AOutput.send(player, "&7Set value &f" + stat + " &7for player &f" + targetPlayer.getName() + " &7to &f" + value.toLowerCase(Locale.ROOT));
//        } else AOutput.error(player, "&cInvalid data type. (int, string, boolean, double)");
//
//        APlayerData.savePlayerData(targetPlayer);
//
//
//
        return false;
    }
}
