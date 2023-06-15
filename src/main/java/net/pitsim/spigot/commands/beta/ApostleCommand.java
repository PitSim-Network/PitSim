package net.pitsim.spigot.commands.beta;

import dev.kyro.arcticapi.commands.ACommand;
import dev.kyro.arcticapi.commands.AMultiCommand;
import dev.kyro.arcticapi.misc.AOutput;
import net.pitsim.spigot.controllers.objects.PitPlayer;
import net.pitsim.spigot.megastreaks.Apostle;
import net.pitsim.spigot.misc.Sounds;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ApostleCommand extends ACommand {
	public ApostleCommand(AMultiCommand base, String executor) {
		super(base, executor);
	}

	@Override
	public void execute(CommandSender sender, Command command, String alias, List<String> args) {
		if(!(sender instanceof Player)) return;
		Player player = (Player) sender;
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

		if(args.size() < 1) {
			AOutput.error(player, "&c&lERROR!&7 Usage: /beta apostle <bonus>");
			return;
		}

		int bonus;
		try {
			bonus = Integer.parseInt(args.get(0));
			if(bonus < 0) throw new NumberFormatException();
		} catch (NumberFormatException e) {
			AOutput.error(player, "&c&lERROR!&7 Invalid number!");
			return;
		}

		if(bonus > Apostle.getMaxMaxXPIncrease()) {
			AOutput.error(player, "&c&lERROR!&7 The max " + Apostle.INSTANCE.getCapsDisplayName() + "&7 bonus is &b+" +
					Apostle.getMaxMaxXPIncrease() + " max XP&7!");
			return;
		}

		pitPlayer.apostleBonus = bonus;
		Sounds.SUCCESS.play(player);
		AOutput.send(player, "&a&lSUCCESS!&7 Set your " + Apostle.INSTANCE.getCapsDisplayName() + "&7 bonus to &b+" + bonus + " max XP&7!");
	}

	@Override
	public List<String> getTabComplete(Player player, String current, List<String> args) {
		return null;
	}
}
