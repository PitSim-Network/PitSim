package dev.kyro.pitsim.commands;

import dev.kyro.pitsim.inventories.DonatorGUI;
import dev.kyro.pitsim.inventories.PerkGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DonatorCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if(!(sender instanceof Player)) return false;
        Player player = (Player) sender;

        DonatorGUI donatorGUI = new DonatorGUI(player);
        donatorGUI.open();

        return false;
    }
}
