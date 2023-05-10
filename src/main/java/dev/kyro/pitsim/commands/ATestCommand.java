package dev.kyro.pitsim.commands;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.misc.effects.SelectiveDrop;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ATestCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;
		if(!player.isOp()) return false;

		SelectiveDrop selectiveDrop = new SelectiveDrop(player.getInventory().getItemInHand(), player.getLocation());
		selectiveDrop.dropItem();

		new BukkitRunnable() {
			@Override
			public void run() {
				selectiveDrop.addPlayer(player);
			}
		}.runTaskLater(PitSim.INSTANCE, 80);

		return false;
	}
}










