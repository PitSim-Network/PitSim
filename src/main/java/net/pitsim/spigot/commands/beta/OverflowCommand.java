package net.pitsim.spigot.commands.beta;

import dev.kyro.arcticapi.commands.ACommand;
import dev.kyro.arcticapi.commands.AMultiCommand;
import dev.kyro.arcticapi.misc.AOutput;
import net.pitsim.spigot.controllers.objects.PitPlayer;
import net.pitsim.spigot.misc.Sounds;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class OverflowCommand extends ACommand {
	public OverflowCommand(AMultiCommand base, String executor) {
		super(base, executor);
	}

	@Override
	public void execute(CommandSender sender, Command command, String alias, List<String> args) {
		if(!(sender instanceof Player)) return;
		Player player = (Player) sender;

		if(args.size() < 1) {
			AOutput.error(player, "&c&lERROR!&7 Usage: /beta overflow <overflow>");
			return;
		}

		int overflow;
		try {
			overflow = Integer.parseInt(args.get(0));
			if(overflow < 0) throw new RuntimeException();
		} catch (NumberFormatException e) {
			AOutput.error(player, "&c&lERROR!&7 Invalid number!");
			return;
		}

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		pitPlayer.overflowXP = overflow;
		Sounds.SUCCESS.play(player);
		AOutput.send(player, "&a&lSUCCESS!&7 Set your &bOverflow XP &7to " + overflow + "&7!");
	}

	@Override
	public List<String> getTabComplete(Player player, String current, List<String> args) {
		return null;
	}
}
