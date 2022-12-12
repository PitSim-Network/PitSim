package dev.kyro.pitsim.commands;

import dev.kyro.pitsim.misc.Base64;
import dev.kyro.pitsim.misc.CustomSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ATestCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;
		if(!player.isOp()) return false;

		System.out.println(Base64.itemTo64(player.getItemInHand()));
		System.out.println(CustomSerializer.serialize(player.getItemInHand()));

		return false;
	}
}