package dev.kyro.pitsim.commands;

import dev.kyro.pitsim.controllers.LockdownManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CaptchaCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player)) return false;
        Player player = (Player) sender;

        if(LockdownManager.captchaPlayers.contains(player) || args.length < 1) return false;
        try {
            UUID uuid = UUID.fromString(args[0]);
            if(!LockdownManager.captchaAnswers.get(player).equals(uuid)) return false;
            LockdownManager.passCaptcha(player);
        } catch(Exception ignored) { }

        return false;
    }
}
