package dev.kyro.pitsim.commands;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ATestCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;
		if(!player.isOp()) return false;

		ItemStack itemStack = player.getItemInHand();
		if(Misc.isAirOrNull(itemStack)) {
			AOutput.error(player, "&c&lERROR!&7 You are not holding an item!");
			return false;
		}

		NBTItem nbtItem = new NBTItem(itemStack);
		System.out.println(nbtItem.getKeys().toString());
		return false;
	}
}