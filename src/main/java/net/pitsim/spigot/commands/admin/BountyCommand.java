package net.pitsim.spigot.commands.admin;

import dev.kyro.arcticapi.commands.ACommand;
import dev.kyro.arcticapi.commands.AMultiCommand;
import dev.kyro.arcticapi.misc.AOutput;
import net.pitsim.spigot.controllers.ChatTriggerManager;
import net.pitsim.spigot.controllers.objects.PitPlayer;
import net.pitsim.spigot.misc.Sounds;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class BountyCommand extends ACommand {
	public BountyCommand(AMultiCommand base, String executor) {
		super(base, executor);
	}

	@Override
	public void execute(CommandSender sender, Command command, String alias, List<String> args) {
		if(!(sender instanceof Player)) return;
		Player player = (Player) sender;
		if(!player.hasPermission("pitsim.bounty")) return;

		if(args.size() < 2) {
			AOutput.error(player, "Usage: /bounty <player> <amount>");
			return;
		}

		for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			if(onlinePlayer.getDisplayName().equalsIgnoreCase(args.get(0))) {
				PitPlayer pitPlayer = PitPlayer.getPitPlayer(onlinePlayer);

				try {
					pitPlayer.bounty += Integer.parseInt(args.get(1));
					ChatTriggerManager.sendBountyInfo(pitPlayer);
				} catch(Exception ignored) {
					AOutput.error(player, "Please enter a valid number");
					return;
				}
				Sounds.BOUNTY.play(onlinePlayer);
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aSuccess!"));
			}
		}
	}

	@Override
	public List<String> getTabComplete(Player player, String current, List<String> args) {
		return null;
	}
}
