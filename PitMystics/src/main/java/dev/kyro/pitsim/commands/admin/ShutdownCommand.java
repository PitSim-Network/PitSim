package dev.kyro.pitsim.commands.admin;

import dev.kyro.arcticapi.commands.ASubCommand;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.ShutdownManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ShutdownCommand extends ASubCommand {
    public ShutdownCommand(String executor) {
        super(executor);
    }
    public Player player;

    @Override
    public void execute(CommandSender sender, List<String> args) {
        player = null;
        if(sender instanceof Player) player = (Player) sender;

        if(args.size() < 1) {
            if(player != null) AOutput.error(player, "&cUsage: /ps shutdown <minutes>");
            return;
        }

        int minutes = 0;
        try {
            minutes = Integer.parseInt(args.get(0));
        } catch (Exception e) {
            if(player != null) AOutput.error(player, "&cInvalid Parameters. Usage: /ps shutdown <minutes>");
            return;
        }

        if(minutes < 1) {
            if(player != null) AOutput.error(player, "&cInvalid Parameters. Usage: /ps shutdown <minutes>");
            return;
        }

        if(ShutdownManager.isShuttingDown) {
            if(player != null) AOutput.error(player, "&cThe server is already shutting down!");
            return;
        }

        if(minutes != 0) {
            ShutdownManager.initiateShutdown(minutes);
            if(player != null) AOutput.send(player, "&aShutdown Initiated!");
        }
    }
}
