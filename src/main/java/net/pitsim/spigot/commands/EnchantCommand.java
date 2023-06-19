package net.pitsim.spigot.commands;

import dev.kyro.arcticapi.misc.AOutput;
import net.pitsim.spigot.PitSim;
import net.pitsim.spigot.items.MysticFactory;
import net.pitsim.spigot.items.PitItem;
import net.pitsim.spigot.controllers.EnchantManager;
import net.pitsim.spigot.controllers.ItemFactory;
import net.pitsim.spigot.controllers.objects.PitEnchant;
import net.pitsim.spigot.enchants.overworld.SelfCheckout;
import net.pitsim.spigot.enums.MysticType;
import net.pitsim.spigot.exceptions.*;
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

		if(!PitSim.isDev()) {
			if(!player.hasPermission("group.nitro")) {
				AOutput.send(player, "&c&lERROR!&7 You must boost our discord server to use this feature!&7 Join with: &f&ndiscord.pitsim.net");
				return false;
			}

			if(MysticType.getMysticType(player.getItemInHand()) == MysticType.TAINTED_CHESTPLATE || MysticType.getMysticType(player.getItemInHand()) == MysticType.TAINTED_SCYTHE) {
				if(!player.isOp()) {
					AOutput.error(player, "&cNice try.");
					return false;
				}
			}
		}

		PitItem pitItem = ItemFactory.getItem(player.getItemInHand());
		if(pitItem == null || !pitItem.isMystic) {
			AOutput.error(player, "&c&lERROR!&7 Not holding a mystic item");
			return false;
		}

		if(MysticFactory.isGemmed(player.getItemInHand())) {
			AOutput.error(player, "&c&lERROR!&7 You cannot modify gemmed items");
			return false;
		}

		if(args.length < 2) {

			AOutput.error(player, "Usage: /enchant <name> <level>");
			return false;
		}

		String refName = args[0].toLowerCase();
		PitEnchant pitEnchant = EnchantManager.getEnchant(refName);
		if(pitEnchant == null) {
			AOutput.error(player, "&c&lERROR!&7 That enchant does not exist");
			return false;
		}

		if(pitEnchant == SelfCheckout.INSTANCE && !player.isOp()) {
			AOutput.error(player, "&cNice try.");
			return false;
		}

		if(!EnchantManager.canTypeApply(player.getItemInHand(), pitEnchant) && !player.isOp()) {
			AOutput.error(player, "&cNice try.");
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
			if(player.isOp() || PitSim.isDev()) {
				updatedItem = EnchantManager.addEnchant(player.getItemInHand(), pitEnchant, level, false);
			} else {
				updatedItem = EnchantManager.addEnchant(player.getItemInHand(), pitEnchant, level, true);
			}
		} catch(Exception exception) {
			if(exception instanceof MismatchedEnchantException) {
				AOutput.error(player, "&c&lERROR!&7 That enchant can't go on that item");
			} else if(exception instanceof InvalidEnchantLevelException) {

				if(!((InvalidEnchantLevelException) exception).levelTooHigh) {
					AOutput.error(player, "&c&lERROR!&7 Level too low");
				} else {
					AOutput.error(player, "&c&lERROR!&7 Level too high");
				}
			} else if(exception instanceof MaxTokensExceededException) {

				if(((MaxTokensExceededException) exception).isRare) {
					AOutput.error(player, "&c&lERROR!&7 You cannot have more than 4 rare tokens on an item");
				} else {
					AOutput.error(player, "&c&lERROR!&7 You cannot have more than 8 tokens on an item");
				}
			} else if(exception instanceof MaxEnchantsExceededException) {

				AOutput.error(player, "&c&lERROR!&7 You cannot have more than 3 enchants on an item");
			} else if(exception instanceof IsJewelException) {
				AOutput.error(player, "&c&lERROR!&7 You cannot modify a jewel enchant");
			} else if(exception instanceof NoCommonEnchantException) {
				AOutput.error(player, "&c&lERROR!&7 You must have at least one common enchant on an item");
			} else {
				exception.printStackTrace();
			}
			return false;
		}

		player.setItemInHand(updatedItem);
		if(level == 0) {
			AOutput.send(player, "&a&lSUCCESS!&7 Removed Enchant: " + pitEnchant.getDisplayName() +
					EnchantManager.enchantLevelToRoman(level));
		} else {
			AOutput.send(player, "&a&lSUCCESS!&7 Added Enchant: " + pitEnchant.getDisplayName() +
					EnchantManager.enchantLevelToRoman(level));
		}
		return false;
	}
}