package net.pitsim.pitsim.commands;

import dev.kyro.arcticapi.misc.AOutput;
import net.pitsim.pitsim.PitSim;
import net.pitsim.pitsim.controllers.DiscordManager;
import net.pitsim.pitsim.controllers.objects.PluginMessage;
import net.pitsim.pitsim.misc.Formatter;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ClaimCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;

		long lastClaim = DiscordManager.getLastBoostRewardClaim(player.getUniqueId());
		if(lastClaim == -1) {
			AOutput.send(player, "&c&lERROR!&7 You do not have a linked discord account. Run /link to link one");
			return false;
		}

		new PluginMessage()
				.writeString("BOOSTER_CLAIM")
				.writeString(PitSim.serverName)
				.writeString(player.getUniqueId().toString())
				.send();
		return false;
	}

	public static void callback(Player player, boolean isBooster) {
		if(!isBooster) {
			AOutput.error(player, "&c&lERROR!&7 You are not boosting the server");
			return;
		}

		long lastClaim = DiscordManager.getLastBoostRewardClaim(player.getUniqueId());
		if(lastClaim == -1) {
			AOutput.send(player, "&c&lERROR!&7 You do not have a linked discord account. Run /link to link one");
			return;
		}

		long nextClaimTime = lastClaim + 1000L * 60 * 60 * 24 * 30;
		long currentTime = System.currentTimeMillis();
		if(nextClaimTime > currentTime) {
			AOutput.send(player, "&c&lERROR!&7 You cannot do this for another " +
					Formatter.formatDurationFull(nextClaimTime - currentTime, true));
			return;
		}

		DiscordManager.setLastBoostRewardClaim(player.getUniqueId(), currentTime);

		new BukkitRunnable() {
			@Override
			public void run() {
				ConsoleCommandSender console = PitSim.INSTANCE.getServer().getConsoleSender();
				Bukkit.dispatchCommand(console, "cc give p basic 1 " + player.getName());
				AOutput.send(player, "&d&lNITRO!&7 Thank you for boosting the server!");
			}
		}.runTask(PitSim.INSTANCE);
	}
}
