package dev.kyro.pitsim.commands;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.pitsim.enums.NBTTag;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetKillCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if(!(sender instanceof Player)) return false;
		if(!sender.isOp()) return false;
		Player player = (Player) sender;

		NBTItem nbtItem = new NBTItem(player.getItemInHand());
		nbtItem.setInteger(NBTTag.PLAYER_KILLS.getRef(), Integer.valueOf(args[0]));
		player.setItemInHand(nbtItem.getItem());
		return false;
	}
}
