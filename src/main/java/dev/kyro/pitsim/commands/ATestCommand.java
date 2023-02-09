package dev.kyro.pitsim.commands;

import dev.kyro.pitsim.aitems.PitItem;
import dev.kyro.pitsim.controllers.ItemFactory;
import dev.kyro.pitsim.controllers.TaintedEnchanting;
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
//		if(!player.isOp()) return false;

		for(int i = 0; i < player.getInventory().getContents().length; i++) {
			ItemStack content = player.getInventory().getItem(i);

			if(Misc.isAirOrNull(content)) continue;
			PitItem pitItem = ItemFactory.getItem(content);
			if(pitItem == null || !pitItem.isMystic) continue;

			ItemStack itemStack = content;

			for(int j = 0; j < 3; j++) {
				itemStack = TaintedEnchanting.enchantItem(itemStack);
			}

			player.getInventory().setItem(i, itemStack);
		}

//		ItemStack itemStack = player.getItemInHand();
//		player.setItemInHand(TaintedEnchanting.enchantItem(itemStack));

//		if(itemStack == null || !itemStack.hasItemMeta() || itemStack.getType() == Material.AIR) {
//			MarketGUI marketGUI = new MarketGUI(player);
//			marketGUI.open();
//			return true;
//		}

		return false;
	}
}