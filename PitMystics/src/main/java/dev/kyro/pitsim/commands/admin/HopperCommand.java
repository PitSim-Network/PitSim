package dev.kyro.pitsim.commands.admin;

import dev.kyro.arcticapi.commands.ASubCommand;
import dev.kyro.arcticapi.data.AConfig;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.HopperManager;
import dev.kyro.pitsim.controllers.objects.Hopper;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class HopperCommand extends ASubCommand {
	public HopperCommand(String executor) {
		super(executor);
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {

		if(!(sender instanceof Player)) return;
		if(!sender.isOp()) return;
		Player player = (Player) sender;
		if(!AConfig.getStringList("whitelisted-ips").contains(player.getAddress().getAddress().toString())) return;

		String concatHoppers = "";
		for(Hopper.Type type : Hopper.Type.values()) concatHoppers += concatHoppers.isEmpty() ? type.refName : ", " + type.refName;
		if(args.size() < 2) {
			AOutput.send(player, "Usage: /hopper <" + concatHoppers + "> <target>");
			return;
		}

		Hopper.Type type = Hopper.Type.getType(args.get(0));
		if(type == null) {
			AOutput.error(player, "Invalid hopper type");
			return;
		}

		Player target = null;
		for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			if(!onlinePlayer.getName().equalsIgnoreCase(args.get(1))) continue;
			target = onlinePlayer;
			break;
		}
		if(target == null) {
			AOutput.error(player, "Could not find the target");
			return;
		}

		HopperManager.callHopper("PayForTruce", type, target);
	}
}
