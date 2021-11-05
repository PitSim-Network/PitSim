package dev.kyro.pitsim.commands;

import dev.kyro.arcticapi.data.AConfig;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.HopperManager;
import dev.kyro.pitsim.controllers.objects.Hopper;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HopperCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if(!(sender instanceof Player)) return false;
		if(!sender.isOp()) return false;
		Player player = (Player) sender;
		if(!AConfig.getStringList("whitelisted-ips").contains(player.getAddress().getAddress().toString())) return false;

		String concatHoppers = "";
		for(Hopper.Type type : Hopper.Type.values()) concatHoppers += concatHoppers.isEmpty() ? type.refName : ", " + type.refName;
		if(args.length < 2) {
			AOutput.send(player, "Usage: /hopper <" + concatHoppers + "> <target>");
			return false;
		}

		Hopper.Type type = Hopper.Type.getType(args[0]);
		if(type == null) {
			AOutput.error(player, "Invalid hopper type");
			return false;
		}

		Player target = null;
		for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			if(!onlinePlayer.getName().equalsIgnoreCase(args[1])) continue;
			target = onlinePlayer;
			break;
		}
		if(target == null) {
			AOutput.error(player, "Could not find the target");
			return false;
		}

		HopperManager.createHopper("PayForTruce", type, target);

		return false;
	}
}
