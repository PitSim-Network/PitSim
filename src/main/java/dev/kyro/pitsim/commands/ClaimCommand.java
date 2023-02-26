package dev.kyro.pitsim.commands;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.DiscordManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClaimCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;

		long lastClaim = DiscordManager.getLastBoostRewardClaim(player.getUniqueId());
		long nextClaimTime = lastClaim + 1000 * 60 * 60 * 30;
		long currentTime = System.currentTimeMillis();
		if(nextClaimTime > currentTime) {
			String timeLeft = (currentTime - nextClaimTime) / 1000.0 / 60 / 60 / 24 + "d";
			AOutput.send(player, "&c&lERROR!&7 You cannot do this for another " + timeLeft);
			return false;
		}

		return false;
	}
}
