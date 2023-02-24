package dev.kyro.pitsim.commands;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.aitems.MysticFactory;
import dev.kyro.pitsim.inventories.TestEnchantGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TestEnchantCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
		if(!(commandSender instanceof Player)) return false;
		Player player = (Player) commandSender;

		if(!player.hasPermission("pitsim.admin")) return false;

		if(args.length < 1) {
			AOutput.error(player, "&cUsage: /testEnchant <mysticType>");
			return false;
		}

		String mysticType = args[0];
		ItemStack mystic = MysticFactory.getFreshItem(player, mysticType);
		if(mystic == null) {
			AOutput.error(player, "&cInvalid mystic type!");
			return false;
		}

		TestEnchantGUI gui = new TestEnchantGUI(player, mysticType);
		gui.open();

		return false;
	}
}
