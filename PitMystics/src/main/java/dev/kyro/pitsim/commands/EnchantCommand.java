package dev.kyro.pitsim.commands;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.exceptions.*;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EnchantCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;

		if(!player.hasPermission("group.nitro")) {
			AOutput.send(player, "&cYou must boost our discord server to gain access to this feature! &7Join with: &f&ndiscord.gg/pitsim");
			return false;
		}

		if(Misc.isAirOrNull(player.getItemInHand())) {

			AOutput.error(player, "Not holding a mystic item");
			return false;
		}
		NBTItem nbtItem = new NBTItem(player.getItemInHand());
		if(!nbtItem.hasKey(NBTTag.ITEM_UUID.getRef())) {

			AOutput.error(player, "Not holding a mystic item");
			return false;
		}

		if(args.length < 2) {

			AOutput.error(player, "Usage: /enchant <name> <level>");
			return false;
		}

		String refName = args[0].toLowerCase();
		PitEnchant pitEnchant = EnchantManager.getEnchant(refName);
		if(pitEnchant == null) {

			AOutput.error(player, "That enchant does not exist");
			return false;
		}

		int level;
		try {
			level = Integer.parseInt(args[1]);
		} catch(Exception ignored) {
			AOutput.error(player, "Usage: /enchant <name> <level>");
			return false;
		}

		ItemStack updatedItem;
		try {
			if(player.isOp()) {
				updatedItem = EnchantManager.addEnchant(player.getItemInHand(), pitEnchant, level, false);
			} else {
				updatedItem = EnchantManager.addEnchant(player.getItemInHand(), pitEnchant, level, true);
			}
		} catch(Exception exception) {
			if(exception instanceof MismatchedEnchantException) {
				AOutput.error(player, "That enchant can't go on that item");
			} else if(exception instanceof InvalidEnchantLevelException) {

				if(!((InvalidEnchantLevelException) exception).levelTooHigh) {
					AOutput.error(player, "Level too low");
				} else {
					AOutput.error(player, "Level too high");
				}
			} else if(exception instanceof MaxTokensExceededException) {

				if(((MaxTokensExceededException) exception).isRare) {
					AOutput.error(player, "You cannot have more than 4 rare tokens on an item");
				} else {
					AOutput.error(player, "You cannot have more than 8 tokens on an item");
				}
			} else if(exception instanceof MaxEnchantsExceededException) {

				AOutput.error(player, "You cannot have more than 3 enchants on an item");
			} else if(exception instanceof IsJewelException) {
				AOutput.error(player, "You cannot modify a jewel enchant");
			} else if(exception instanceof NoCommonEnchantException) {
				AOutput.error(player, "You must have at least one common enchant on an item");
			} else {
				exception.printStackTrace();
			}
			return false;
		}

		player.setItemInHand(updatedItem);
		AOutput.send(player, "Added the enchant");
		return false;
	}
}