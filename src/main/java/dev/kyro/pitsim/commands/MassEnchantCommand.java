package dev.kyro.pitsim.commands;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.aitems.MysticFactory;
import dev.kyro.pitsim.inventories.MassEnchantGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MassEnchantCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
		if(!(commandSender instanceof Player)) return false;
		Player player = (Player) commandSender;

		if(!PitSim.isDev() && !player.hasPermission("pitsim.admin")) return false;

		if(args.length < 1) {
			AOutput.error(player, "&cUsage: /massenchant <mysticType>");
			return false;
		}

		String mysticType = args[0];
		ItemStack mystic = MysticFactory.getFreshItem(player, mysticType);
		if(mystic == null) {
			AOutput.error(player, "&cInvalid mystic type!");
			return false;
		}

		MassEnchantGUI gui = new MassEnchantGUI(player, mysticType);
		gui.open();

		return false;
	}
}
