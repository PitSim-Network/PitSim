package dev.kyro.pitsim.commands;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.enums.MysticType;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.enums.PantColor;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.UUID;

public class FreshCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;

		if(!player.hasPermission("group.nitro")) {
			AOutput.send(player, "&cYou must boost our discord server to gain access to this feature! &7Join with: &f&ndiscord.gg/pitsim");
			return false;
		}

		if(args.length < 1) {

			AOutput.error(player, "Usage: /enchant <sword|bow|color>");
			return false;
		}

		String type = args[0].toLowerCase();
		ItemStack mystic = getFreshItem(player, type);
		if(mystic == null) {
			AOutput.error(player, "Usage: /enchant <sword|bow|fresh>");
			return false;
		}

		if(MysticType.getMysticType(mystic) == MysticType.TAINTED_CHESTPLATE || MysticType.getMysticType(mystic) == MysticType.TAINTED_SCYTHE) {
			if(!player.isOp()) {
				AOutput.error(player, "&cNice try.");
				return false;
			}
		}

		AUtil.giveItemSafely(player, mystic);
		return false;
	}

	public static ItemStack getFreshItem(Player player, String type) {
		if(type.equals("sword")) {
			return getFreshItem(MysticType.SWORD, null);
		} else if(type.equals("bow")) {
			return getFreshItem(MysticType.BOW, null);
		} else if(type.equals("chestplate")) {
			if(!player.isOp()) return null;
			return getFreshItem(MysticType.TAINTED_CHESTPLATE, null);
		} else if(type.equals("scythe")) {
			if(!player.isOp()) return null;
			return getFreshItem(MysticType.TAINTED_SCYTHE, null);
		} else if(PantColor.getPantColor(type) != null) {
			return getFreshItem(MysticType.PANTS, PantColor.getPantColor(type));
		}

		return null;
	}

	public static ItemStack getFreshItem(MysticType type, PantColor pantColor) {

		ItemStack mystic;
		if(type == MysticType.SWORD) {

			mystic = new AItemStackBuilder(Material.GOLD_SWORD)
					.setName("&eMystic Sword")
					.setLore(new ALoreBuilder("&7Kept on death", "&f", "&f", "&7Used in the mystic well"))
					.addUnbreakable(true).getItemStack();
			mystic.addEnchantment(Enchantment.DAMAGE_ALL, 2);
			ItemMeta itemMeta = mystic.getItemMeta();
			itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			mystic.setItemMeta(itemMeta);
		} else if(type == MysticType.BOW) {

			mystic = new AItemStackBuilder(Material.BOW)
					.setName("&bMystic Bow")
					.setLore(new ALoreBuilder("&7Kept on death", "&f", "&f", "&7Used in the mystic well"))
					.addUnbreakable(true).getItemStack();
		} else if(type == MysticType.TAINTED_SCYTHE) {

			mystic = new AItemStackBuilder(Material.GOLD_HOE)
					.setName("&5Fresh Tainted Scythe")
					.setLore(new ALoreBuilder("&7Kept on death", "&f", "&f", "&7Used in the mystic well"))
					.addUnbreakable(true).getItemStack();
			mystic.addEnchantment(Enchantment.DURABILITY, 1);
			ItemMeta itemMeta = mystic.getItemMeta();
			itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			mystic.setItemMeta(itemMeta);
		} else if(type == MysticType.TAINTED_CHESTPLATE) {

			pantColor = PantColor.TAINTED;
			mystic = new AItemStackBuilder(Material.LEATHER_CHESTPLATE)
					.setName(pantColor.chatColor + "Fresh Tainted Chestplate")
					.setLore(new ALoreBuilder("&7Kept on death", "&f",
							pantColor.chatColor + "Used in the mystic well", pantColor.chatColor + "Also, a fashion statement"))
					.addUnbreakable(true)
					.getItemStack();
			LeatherArmorMeta meta = (LeatherArmorMeta) mystic.getItemMeta();
			meta.setColor(Color.fromRGB(pantColor.hexColor));
			mystic.setItemMeta(meta);
		} else {

			mystic = new AItemStackBuilder(Material.LEATHER_LEGGINGS)
					.setName(pantColor.chatColor + "Fresh " + pantColor.refName + " Pants")
					.setLore(new ALoreBuilder("&7Kept on death", "&f", "&f",
							pantColor.chatColor + "Used in the mystic well", pantColor.chatColor + "Also, a fashion statement"))
					.addUnbreakable(true).getItemStack();
			mystic = PantColor.setPantColor(mystic, pantColor);
		}

		if(mystic == null) return null;
		NBTItem nbtMystic = new NBTItem(mystic);
		nbtMystic.setString(NBTTag.ITEM_UUID.getRef(), UUID.randomUUID().toString());
		nbtMystic.addCompound(NBTTag.PIT_ENCHANTS.getRef());

		return nbtMystic.getItem();
	}

	public static boolean isFresh(ItemStack itemStack) {

		if(Misc.isAirOrNull(itemStack)) return false;

		NBTItem nbtItem = new NBTItem(itemStack);
		if(!nbtItem.hasKey(NBTTag.ITEM_UUID.getRef())) return false;

		ItemMeta itemMeta = itemStack.getItemMeta();
		if(itemMeta == null || !itemMeta.hasLore()) return false;

		for(String line : itemMeta.getLore()) {
			if(line.contains("Used in the mystic well")) return true;
		}
		return false;
	}
}
