package net.pitsim.spigot.commands.admin;

import dev.kyro.arcticapi.commands.ACommand;
import dev.kyro.arcticapi.commands.AMultiCommand;
import dev.kyro.arcticapi.misc.AOutput;
import net.pitsim.spigot.controllers.objects.PitPlayer;
import net.pitsim.spigot.misc.Lang;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class StreakCommand extends ACommand {
	public StreakCommand(AMultiCommand base, String executor) {
		super(base, executor);
	}

	@Override
	public void execute(CommandSender sender, Command command, String alias, List<String> args) {
		if(!(sender instanceof Player)) return;
		Player player = (Player) sender;

		if(args.size() < 1) {
			AOutput.error(sender, "Usage: /streak [player] <amount>");
			return;
		}

		Player target = player;
		int amount;

		if(args.size() == 1) {
			try {
				amount = Integer.parseInt(args.get(0));
			} catch(Exception ignored) {
				AOutput.error(player, "Not a valid number");
				return;
			}
		} else {
			target = Bukkit.getPlayer(args.get(0));
			if(target == null) {
				Lang.COULD_NOT_FIND_PLAYER_WITH_NAME.send(player);
				return;
			}

			try {
				amount = Integer.parseInt(args.get(1));
			} catch(Exception ignored) {
				AOutput.error(player, "Not a valid number");
				return;
			}
		}

		PitPlayer pitTarget = PitPlayer.getPitPlayer(target);
		for(int i = 0; i < amount; i++) pitTarget.incrementKills();
	}

	@Override
	public List<String> getTabComplete(Player player, String current, List<String> args) {
		return null;
	}
}
