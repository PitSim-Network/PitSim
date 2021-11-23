package dev.kyro.pitsim.commands.admin;

import dev.kyro.arcticapi.commands.ASubCommand;
import dev.kyro.arcticapi.misc.AOutput;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ReloadCommand extends ASubCommand {
	public ReloadCommand(String executor) {
		super(executor);
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if(!(sender instanceof Player)) return;
		Player player = (Player) sender;
		if(!player.isOp()) {
			AOutput.error(player, "&cInsufficient Permissions");
			return;
		}

		Bukkit.getServer().dispatchCommand(player, "plugman unload pitdiscord");
		Bukkit.getServer().dispatchCommand(player, "plugman reload pitremake");
		Bukkit.getServer().dispatchCommand(player, "plugman load Discord-1.0.0-all");
	}
}
