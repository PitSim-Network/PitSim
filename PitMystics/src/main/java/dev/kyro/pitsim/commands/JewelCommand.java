package dev.kyro.pitsim.commands;

import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.enums.MysticType;
import dev.kyro.pitsim.enums.PantColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class JewelCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;

		ItemStack jewel = FreshCommand.getFreshItem(MysticType.PANTS, PantColor.RED);
		List<PitEnchant> applicableEnchants = EnchantManager.getEnchants(ApplyType.PANTS);
		PitEnchant jewelEnchant = applicableEnchants.get((int) (Math.random() * applicableEnchants.size()));
		try {
			jewel = EnchantManager.addEnchant(jewel, jewelEnchant, 3, false, true);
		} catch(Exception ignored) { }

		AUtil.giveItemSafely(player, jewel);
		return false;
	}
}
