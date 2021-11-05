package dev.kyro.pitsim.commands;

import dev.kyro.arcticapi.commands.ASubCommand;
import dev.kyro.arcticapi.data.AConfig;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.NonAnticheat;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.List;

public class AnticheatCommand extends ASubCommand {
	public AnticheatCommand(String executor) {
		super(executor);
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if(!(sender instanceof Player)) return;
		Player player = ((Player) sender).getPlayer();
		if(!player.isOp()) return;
		if(!AConfig.getStringList("whitelisted-ips").contains(player.getAddress().getAddress().toString())) return;

		if(args.size() < 1) {
			AOutput.error(player, "Usage: /check <player>");
			return;
		}

		Player target = null;
		for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			if(!onlinePlayer.getName().equalsIgnoreCase(args.get(0))) continue;
			target = onlinePlayer;
			break;
		}
		if(target == null) {
			AOutput.error(player, "Could not find that player");
			return;
		}

		NonAnticheat.AnticheatData anticheatData = NonAnticheat.getPlayerLogs(target);
		if(!anticheatData.hasLogs()) {
			AOutput.send(player, "That player has no logs");
			return;
		}

		DecimalFormat format = new DecimalFormat("0.00");
		AOutput.send(player, "&6ANTICHEAT LOGS: &e" + target.getName() + " &7(p: " + ((CraftPlayer) player).getHandle().ping + "ms)");
		AOutput.send(player, "&6Recent Hits: &e" + anticheatData.getRecentHits());
		AOutput.send(player, "&6Abnormal hit distance: &e" + format.format(anticheatData.getAbnormalDistancePercent()) + "%");
		AOutput.send(player, "&6Abnormal hit angle: &e" + format.format(anticheatData.getAbnormalAnglePercent()) + "%");
	}
}
