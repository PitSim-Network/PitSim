package dev.kyro.pitsim.commands;

import dev.kyro.pitsim.controllers.MobManager;
import dev.kyro.pitsim.mobs.PitZombie;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ATestCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;

		new PitZombie(player.getLocation());
		Bukkit.broadcastMessage(MobManager.mobs + "");

//		AOutput.send(sender, "Running dupe manager");
//		DupeManager.run();

		return false;
	}
}