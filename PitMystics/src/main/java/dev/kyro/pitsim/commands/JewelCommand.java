package dev.kyro.pitsim.commands;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.ItemManager;
import dev.kyro.pitsim.enums.MysticType;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.enums.PantColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class JewelCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;

		MysticType mysticType;
		int rand = (int) (Math.random() * 3);
		switch(rand) {
			case 0:
				mysticType = MysticType.SWORD;
				break;
			case 1:
				mysticType = MysticType.BOW;
				break;
			default:
				mysticType = MysticType.PANTS;
				break;
		}

		ItemStack jewel = FreshCommand.getFreshItem(mysticType, PantColor.JEWEL);
		jewel = ItemManager.enableDropConfirm(jewel);
		assert jewel != null;
		NBTItem nbtItem = new NBTItem(jewel);
		nbtItem.setBoolean(NBTTag.IS_JEWEL.getRef(), true);
		EnchantManager.setItemLore(nbtItem.getItem());

		AUtil.giveItemSafely(player, nbtItem.getItem());
		return false;
	}
}
