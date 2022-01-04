package dev.kyro.pitsim.commands;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PayCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if(!(sender instanceof Player)) return false;
        Player player = (Player) sender;

        if(args.length < 2) {
            AOutput.error(player, "Usage: /pay <player> <amount>");
            return false;
        }

        PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
        if(pitPlayer.level < 100) {
            AOutput.error(player, "&c&lNOPE! &7You cannot trade until level 100");
            return false;
        }

        Player target = null;
        for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if(!player.getName().equalsIgnoreCase(args[0])) continue;
            target = onlinePlayer;
            break;
        }
        if(target == null) {
            AOutput.error(player, "&c&lNOPE! &7Could not find that player");
            return false;
        } else if(target == player) {
            AOutput.error(player, "&c&lNOPE! &7You cannot pay yourself");
            return false;
        }

        PitPlayer pitTarget = PitPlayer.getPitPlayer(target);
        if(pitTarget.level < 100) {
            AOutput.error(player, "&c&lNOPE! &7That player is not level 100+");
            return false;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[1]);
            if(amount <= 0) throw new IllegalArgumentException();
        } catch(Exception ignored) {
            AOutput.error(player, "Invalid amount of money");
            return false;
        }

        PitSim.VAULT.withdrawPlayer(player, amount);
        PitSim.VAULT.depositPlayer(target, amount);
        AOutput.send(player, "&6&lTRADE! &7You have sent &6" + target.getName() + " &7$" + amount);
        AOutput.send(target, "&6&lTRADE! &7You have received $" + amount + " from &7" + player.getName());
        return false;
    }
}