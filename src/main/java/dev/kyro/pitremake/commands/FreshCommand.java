package dev.kyro.pitremake.commands;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitremake.enums.NBTTag;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
		} else {

			AOutput.error(player, "Usage: /enchant <sword|bow|fresh>");
			return false;
		}

		NBTItem nbtItem = new NBTItem(itemStack);
		nbtItem.setString(NBTTag.ITEM_UUID.getRef(), UUID.randomUUID().toString());

		return false;
	}
}
