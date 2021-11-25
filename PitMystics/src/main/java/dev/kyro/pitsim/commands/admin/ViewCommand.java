package dev.kyro.pitsim.commands.admin;

import dev.kyro.arcticapi.commands.ASubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ViewCommand extends ASubCommand {
    public ViewCommand(String executor) {
        super(executor);
    }

    @Override
    public void execute(CommandSender sender, List<String> args) {
        if(!(sender instanceof Player)) return;
        Player player = (Player) sender;


    }
}
