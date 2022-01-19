package dev.kyro.pitsim.commands.admin;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.commands.ASubCommand;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.ShutdownManager;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class ShutdownCommand extends ASubCommand {
    public ShutdownCommand(String executor) {
        super(executor);
    }

    @Override
    public void execute(CommandSender sender, List<String> args) {
        if(!(sender instanceof Player)) return;
        Player player = (Player) sender;

        if(args.size() < 1) {
            AOutput.error(player, "&cUsage: /ps shutdown <minutes>");
            return;
        }

        int minutes = 0;
        try {
            minutes = Integer.parseInt(args.get(0));
        } catch (Exception e) {
            AOutput.error(player, "&cInvalid Parameters. Usage: /ps shutdown <minutes>");
            return;
        }

        if(ShutdownManager.isShuttingDown) {
            AOutput.error(player, "&cThe server is already shutting down!");
            return;
        }

        if(minutes != 0) {
            ShutdownManager.initiateShutdown(minutes);
            AOutput.send(player, "&aShutdown Initiated!");
        }
    }
}
