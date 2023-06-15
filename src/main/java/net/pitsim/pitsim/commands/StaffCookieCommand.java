package net.pitsim.pitsim.commands;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import net.pitsim.pitsim.aitems.misc.StaffCookie;
import net.pitsim.pitsim.controllers.ItemFactory;
import net.pitsim.pitsim.misc.Lang;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class StaffCookieCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;

		if(!player.hasPermission("pitsim.cookie")) {
			Lang.NO_PERMISSION.send(player);
			return false;
		}

		if(args.length < 1) {
			AOutput.error(player, "&c&lERROR!&7 Usage: /" + label + " <receiver>");
			return false;
		}

		Player target = Bukkit.getPlayer(args[0]);
		if(target == null) {
			Lang.COULD_NOT_FIND_PLAYER_WITH_NAME.send(player);
			return false;
		}

		ItemStack itemStack = ItemFactory.getItem(StaffCookie.class).getItem(player, target, 1);
		AUtil.giveItemSafely(player, itemStack, true);
		return false;
	}
}
