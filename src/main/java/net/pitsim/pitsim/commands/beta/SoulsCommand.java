package net.pitsim.pitsim.commands.beta;

import dev.kyro.arcticapi.commands.ACommand;
import dev.kyro.arcticapi.commands.AMultiCommand;
import dev.kyro.arcticapi.misc.AOutput;
import net.pitsim.pitsim.controllers.objects.PitPlayer;
import net.pitsim.pitsim.misc.Sounds;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SoulsCommand extends ACommand {
	public SoulsCommand(AMultiCommand base, String executor) {
		super(base, executor);
	}

	@Override
	public void execute(CommandSender sender, Command command, String alias, List<String> args) {
		if(!(sender instanceof Player)) return;
		Player player = (Player) sender;

		if(args.size() < 1) {
			AOutput.error(player, "&c&lERROR!&7 Usage: /beta souls <amount>");
			return;
		}

		int souls;
		try {
			souls = Integer.parseInt(args.get(0));
		} catch (NumberFormatException e) {
			AOutput.error(player, "&c&lERROR!&7 Invalid number!");
			return;
		}

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		pitPlayer.giveSouls(souls, false);
		Sounds.SUCCESS.play(player);
		AOutput.send(player, "&a&lSUCCESS!&7 Added &f" + souls + " souls &7to your account!");
	}

	@Override
	public List<String> getTabComplete(Player player, String current, List<String> args) {
		return null;
	}
}
