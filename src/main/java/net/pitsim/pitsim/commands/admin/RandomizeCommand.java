package net.pitsim.pitsim.commands.admin;

import dev.kyro.arcticapi.commands.ACommand;
import dev.kyro.arcticapi.commands.AMultiCommand;
import dev.kyro.arcticapi.misc.AOutput;
import net.pitsim.pitsim.aitems.PitItem;
import net.pitsim.pitsim.controllers.ItemFactory;
import net.pitsim.pitsim.misc.Misc;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class RandomizeCommand extends ACommand {
	public RandomizeCommand(AMultiCommand base, String executor) {
		super(base, executor);
	}

	@Override
	public void execute(CommandSender sender, Command command, String alias, List<String> args) {
		if(!(sender instanceof Player)) return;
		Player player = (Player) sender;

		if(Misc.isAirOrNull(player.getItemInHand())) {
			AOutput.error(player, "&cInvalid item!");
			return;
		}

		PitItem pitItem = ItemFactory.getItem(player.getItemInHand());
		if(pitItem == null || !pitItem.hasUUID) {
			AOutput.error(player, "&cERROR!&7 That item should not have a UUID");
			return;
		}

		ItemStack heldStack = player.getItemInHand();
		heldStack = pitItem.randomizeUUID(heldStack);
		player.getInventory().setItemInHand(heldStack);
		player.updateInventory();
	}

	@Override
	public List<String> getTabComplete(Player player, String current, List<String> args) {
		return null;
	}
}
