package dev.kyro.pitsim.commands.admin;

import dev.kyro.arcticapi.commands.ASubCommand;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class BountyCommand extends ASubCommand {
    public BountyCommand(String executor) {
        super(executor);
    }

    @Override
    public void execute(CommandSender sender, List<String> args) {
        if(!(sender instanceof Player)) return;
        Player player = (Player) sender;
        if(!player.hasPermission("pitsim.bounty")) return;

        if(args.size() < 2) {
            AOutput.error(player, "Usage: /bounty <player> <amount>");
            return;
        }

        for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if(onlinePlayer.getDisplayName().equalsIgnoreCase(args.get(0))) {
                PitPlayer pitPlayer = PitPlayer.getPitPlayer(onlinePlayer);

                try {
                    pitPlayer.bounty += Integer.parseInt(args.get(1));
                } catch(Exception ignored) {
                    AOutput.error(player, "Please enter a valid number");
                    return;
                }
                Sounds.BOUNTY.play(onlinePlayer);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aSuccess!"));
            }
        }
    }
}
