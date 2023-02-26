package dev.kyro.pitsim.commands;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.DiscordManager;
import dev.kyro.pitsim.controllers.objects.PluginMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

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

		long nextClaimTime = lastClaim + 1000 * 60 * 60 * 30;
		long currentTime = System.currentTimeMillis();
		if(nextClaimTime > currentTime) {
			String timeLeft = (currentTime - nextClaimTime) / 1000.0 / 60 / 60 / 24 + "d";
			AOutput.send(player, "&c&lERROR!&7 You cannot do this for another " + timeLeft);
			return;
		}

		DiscordManager.setLastBoostRewardClaim(player.getUniqueId(), currentTime);

		ConsoleCommandSender console = PitSim.INSTANCE.getServer().getConsoleSender();
		Bukkit.dispatchCommand(console, "cc give p basic 1 " + player.getName());
		AOutput.send(player, "&dNITRO!&7 Thank you for boosting the server!");
	}
}
