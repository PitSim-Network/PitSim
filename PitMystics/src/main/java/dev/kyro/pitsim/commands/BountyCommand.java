package dev.kyro.pitsim.commands;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.ASound;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
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
                ASound.play(onlinePlayer, Sound.WITHER_SPAWN, 1, 1);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aSuccess!"));
            }
        }
        return false;
    }
}
