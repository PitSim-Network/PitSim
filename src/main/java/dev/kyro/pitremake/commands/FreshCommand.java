package dev.kyro.pitremake.commands;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitremake.enums.NBTTag;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.UUID;

public class FreshCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;

		if(args.length < 1) {

			AOutput.error(player, "Usage: /enchant <sword|bow|fresh>");
			return false;
		}
		String type = args[0].toLowerCase();

		ItemStack itemStack;
		if(type.equals("sword")) {

			itemStack = new AItemStackBuilder(Material.GOLD_SWORD)
					.setName("&eMystic Sword")
					.setLore(new ALoreBuilder("&7Kept on death", "&f", "&f", "&7Used in the mystic well"))
					.addUnbreakable(true).getItemStack();
		} else if(type.equals("bow")) {

			itemStack = new AItemStackBuilder(Material.BOW)
					.setName("&bMystic Bow")
					.setLore(new ALoreBuilder("&7Kept on death", "&f", "&f", "&7Used in the mystic well"))
					.addUnbreakable(true).getItemStack();
		} else if(type.equals("pants")) {

			itemStack = new AItemStackBuilder(Material.LEATHER_LEGGINGS)
					.setName("&9Fresh Blue Pants")
					.setLore(new ALoreBuilder("&7Kept on death", "&f", "&f", "&9Used in the mystic well", "&9Also, a fashion statement"))
					.addUnbreakable(true).getItemStack();
			LeatherArmorMeta meta = (LeatherArmorMeta) itemStack.getItemMeta();
			meta.setColor(Color.BLUE);
			itemStack.setItemMeta(meta);
		} else {

			AOutput.error(player, "Usage: /enchant <sword|bow|fresh>");
			return false;
		}

		NBTItem nbtItem = new NBTItem(itemStack);
		nbtItem.setString(NBTTag.ITEM_UUID.getRef(), UUID.randomUUID().toString());

		nbtItem.addCompound(NBTTag.PIT_ENCHANTS.getRef());

		AUtil.giveItemSafely(player, nbtItem.getItem());

		return false;
	}
}
