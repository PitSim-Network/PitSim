package net.pitsim.pitsim.commands.essentials;

import dev.kyro.arcticapi.misc.AOutput;
import net.pitsim.pitsim.misc.Lang;
import net.pitsim.pitsim.misc.Misc;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RenameCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;
		if(!player.hasPermission("pitsim.rename")) {
			Lang.NO_PERMISSION.send(player);
			return false;
		}

		if(args.length < 1) {
			AOutput.error(player, "&c&lERROR!&7 Usage: /" + label + " <name>");
			return false;
		}

		ItemStack itemStack = player.getItemInHand();
		if(Misc.isAirOrNull(itemStack)) {
			AOutput.error(player, "&c&lERROR!&7 You are not holding an item");
			return false;
		}

		ItemMeta itemMeta = itemStack.hasItemMeta() ? itemStack.getItemMeta() : Bukkit.getItemFactory().getItemMeta(itemStack.getType());
		itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', String.join(" ", args)));
		itemStack.setItemMeta(itemMeta);
		player.setItemInHand(itemStack);
		player.updateInventory();
		return false;
	}
}
