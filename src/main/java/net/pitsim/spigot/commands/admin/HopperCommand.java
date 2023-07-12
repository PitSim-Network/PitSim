package net.pitsim.spigot.commands.admin;

import dev.kyro.arcticapi.commands.ACommand;
import dev.kyro.arcticapi.commands.AMultiCommand;
import dev.kyro.arcticapi.misc.AOutput;
import net.pitsim.spigot.controllers.HopperManager;
import net.pitsim.spigot.controllers.objects.Hopper;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class HopperCommand extends ACommand {
	public HopperCommand(AMultiCommand base, String executor) {
		super(base, executor);
	}

	@Override
	public void execute(CommandSender sender, Command command, String alias, List<String> args) {
		if(!(sender instanceof Player)) return;
		Player player = (Player) sender;
		if(!player.isOp()) return;
//		if(!FirestoreManager.CONFIG.whitelistedIPs.contains(player.getAddress().getAddress().toString())) return;

		String concatHoppers = "";
		for(Hopper.Type type : Hopper.Type.values())
			concatHoppers += concatHoppers.isEmpty() ? type.refName : ", " + type.refName;
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

	@Override
	public List<String> getTabComplete(Player player, String current, List<String> args) {
		return null;
	}
}
