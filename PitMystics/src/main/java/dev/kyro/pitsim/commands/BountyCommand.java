package dev.kyro.pitsim.commands;

import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BountyCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if(!(sender instanceof Player)) return false;

        Player player = (Player) sender;

        if(!player.hasPermission("pitsim.bounty")) return false;

        for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if(onlinePlayer.getDisplayName().equalsIgnoreCase(args[0])) {
                PitPlayer pitPlayer = PitPlayer.getPitPlayer(onlinePlayer);

                pitPlayer.bounty += Integer.parseInt(args[1]);
                Sounds.BOUNTY.play(onlinePlayer);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aSuccess!"));
            }
        }
        return false;
    }
}
