package dev.kyro.pitsim.commands.admin;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.commands.ACommand;
import dev.kyro.arcticapi.commands.AMultiCommand;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

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

		NBTItem nbtItem = new NBTItem(player.getItemInHand());

		if(!nbtItem.hasKey(NBTTag.ITEM_UUID.getRef())) {
			AOutput.error(player, "&cInvalid item!");
			return;
		}

		EnchantManager.setItemLore(nbtItem.getItem());

		nbtItem.setString(NBTTag.ITEM_UUID.getRef(), UUID.randomUUID().toString());
		player.getInventory().setItemInHand(nbtItem.getItem());
	}

	@Override
	public List<String> getTabComplete(Player player, String current, List<String> args) {
		return null;
	}
}
