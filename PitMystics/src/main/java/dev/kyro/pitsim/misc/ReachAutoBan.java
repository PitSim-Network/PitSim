package dev.kyro.pitsim.misc;

import dev.kyro.pitsim.PitSim;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.stellardev.anticheat.api.BuzzAlertEvent;

public class ReachAutoBan implements Listener {



	@EventHandler
	public void onFlag(BuzzAlertEvent event) {
		if(event.getCheck().equalsIgnoreCase("RangeA") && event.getTotalVl() >= 4) {
			ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
			String command = "ban " + event.getPlayer().getName() + " 7d &f[MYSTIC CHEAT DETECTION]";
			if(!Bukkit.isPrimaryThread())Bukkit.getScheduler().runTask(PitSim.INSTANCE, () -> Bukkit.dispatchCommand(console, command));
			else Bukkit.dispatchCommand(console, command);
		}
	}
}
