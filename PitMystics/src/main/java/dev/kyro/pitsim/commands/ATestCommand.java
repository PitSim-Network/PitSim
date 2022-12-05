package dev.kyro.pitsim.commands;

import dev.kyro.pitsim.misc.Base64;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import storage.StorageManager;
import storage.StorageProfile;

public class ATestCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;
		if(!player.isOp()) return false;

		int page = Integer.parseInt(args[0]);

		StorageProfile profile = StorageManager.getProfile(player);
		Inventory inv = profile.getEnderchest(page);

		player.openInventory(inv);

//		ItemStack itemStack = player.getItemInHand();
//		System.out.println(Base64.itemTo64(itemStack));

		return false;
	}
}