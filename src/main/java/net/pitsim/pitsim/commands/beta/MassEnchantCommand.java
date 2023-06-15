package net.pitsim.pitsim.commands.beta;

import dev.kyro.arcticapi.commands.ACommand;
import dev.kyro.arcticapi.commands.AMultiCommand;
import dev.kyro.arcticapi.misc.AOutput;
import net.pitsim.pitsim.aitems.MysticFactory;
import net.pitsim.pitsim.inventories.MassEnchantGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class MassEnchantCommand extends ACommand {
	public MassEnchantCommand(AMultiCommand base, String executor) {
		super(base, executor);
	}

	@Override
	public void execute(CommandSender sender, Command command, String alias, List<String> args) {
		if(!(sender instanceof Player)) return;
		Player player = (Player) sender;

		if(args.size() < 1) {
			AOutput.error(player, "&cUsage: /massenchant <mystic-type>");
			return;
		}

		String mysticType = args.get(0);
		ItemStack mystic = MysticFactory.getFreshItem(player, mysticType);
		if(mystic == null) {
			AOutput.error(player, "&cInvalid mystic type!");
			return;
		}

		MassEnchantGUI gui = new MassEnchantGUI(player, mysticType);
		gui.open();
	}

	@Override
	public List<String> getTabComplete(Player player, String current, List<String> args) {
		return null;
	}
}
