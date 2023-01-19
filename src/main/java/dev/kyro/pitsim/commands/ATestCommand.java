package dev.kyro.pitsim.commands;

import dev.kyro.pitsim.adarkzone.progression.ProgressionGUI;
import dev.kyro.pitsim.controllers.objects.PluginMessage;
import dev.kyro.pitsim.market.MarketGUI;
import dev.kyro.pitsim.storage.StorageProfile;
import org.bukkit.Material;
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
//		if(!player.isOp()) return false;

		ItemStack itemStack = player.getItemInHand();
		if(itemStack == null || !itemStack.hasItemMeta() || itemStack.getType() == Material.AIR) {
			MarketGUI marketGUI = new MarketGUI(player);
			marketGUI.open();
			return true;
		}

		return false;
	}
}