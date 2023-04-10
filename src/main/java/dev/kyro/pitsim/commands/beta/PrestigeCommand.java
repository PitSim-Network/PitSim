package dev.kyro.pitsim.commands.beta;

import dev.kyro.arcticapi.commands.ACommand;
import dev.kyro.arcticapi.commands.AMultiCommand;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class PrestigeCommand extends ACommand {
	public PrestigeCommand(AMultiCommand base, String executor) {
		super(base, executor);
	}

	@Override
	public void execute(CommandSender sender, Command command, String alias, List<String> args) {
		if(!(sender instanceof Player)) return;
		Player player = (Player) sender;

		if(args.size() < 1) {
			AOutput.error(player, "&c&lERROR!&7 Usage: /beta prestige <prestige>");
			return;
		}

		int prestige;
		try {
			prestige = Integer.parseInt(args.get(0));
			if(prestige < 0 || prestige > 60) throw new RuntimeException();
		} catch (NumberFormatException e) {
			AOutput.error(player, "&c&lERROR!&7 Invalid number!");
			return;
		}

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		pitPlayer.prestige = prestige;
		Sounds.SUCCESS.play(player);
		AOutput.send(player, "&a&lSUCCESS!&7 Set your &bPrestige &7to " + prestige + "&7!");
	}

	@Override
	public List<String> getTabComplete(Player player, String current, List<String> args) {
		return null;
	}
}
