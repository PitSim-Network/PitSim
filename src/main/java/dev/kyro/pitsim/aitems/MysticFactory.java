package dev.kyro.pitsim.aitems;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.pitsim.aitems.mystics.*;
import dev.kyro.pitsim.controllers.ItemFactory;
import dev.kyro.pitsim.enums.MysticType;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.enums.PantColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MysticFactory {
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
			return ItemFactory.getItem(MysticSword.class).getItem();

//			mystic = new AItemStackBuilder(Material.GOLD_SWORD)
//					.setName("&eMystic Sword")
//					.setLore(new ALoreBuilder("&7Kept on death", "&f", "&f", "&7Used in the mystic well"))
//					.addUnbreakable(true).getItemStack();
//			mystic.addEnchantment(Enchantment.DAMAGE_ALL, 2);
//			ItemMeta itemMeta = mystic.getItemMeta();
//			itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
//			mystic.setItemMeta(itemMeta);
		} else if(type == MysticType.BOW) {
			return ItemFactory.getItem(MysticBow.class).getItem();

//			mystic = new AItemStackBuilder(Material.BOW)
//					.setName("&bMystic Bow")
//					.setLore(new ALoreBuilder("&7Kept on death", "&f", "&f", "&7Used in the mystic well"))
//					.addUnbreakable(true).getItemStack();
		} else if(type == MysticType.TAINTED_SCYTHE) {
			return ItemFactory.getItem(TaintedScythe.class).getItem();

//			mystic = new AItemStackBuilder(Material.GOLD_HOE)
//					.setName("&5Fresh Tainted Scythe")
//					.setLore(new ALoreBuilder("&7Kept on death", "&f", "&f", "&7Used in the mystic well"))
//					.addUnbreakable(true).getItemStack();
//			mystic.addEnchantment(Enchantment.DURABILITY, 1);
//			ItemMeta itemMeta = mystic.getItemMeta();
//			itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
//			mystic.setItemMeta(itemMeta);
//
//			NBTItem nbtItem = new NBTItem(mystic);
////			nbtItem.setBoolean(NBTTag.DROP_CONFIRM.getRef(), true);
//			mystic = nbtItem.getItem();
		} else if(type == MysticType.TAINTED_CHESTPLATE) {
			return ItemFactory.getItem(TaintedChestplate.class).getItem();

//			pantColor = PantColor.TAINTED;
//			mystic = new AItemStackBuilder(Material.LEATHER_CHESTPLATE)
//					.setName(pantColor.chatColor + "Fresh Tainted Chestplate")
//					.setLore(new ALoreBuilder("&7Kept on death", "&f",
//							pantColor.chatColor + "Used in the mystic well", pantColor.chatColor + "Also, a fashion statement"))
//					.addUnbreakable(true)
//					.getItemStack();
//			LeatherArmorMeta meta = (LeatherArmorMeta) mystic.getItemMeta();
//			meta.setColor(Color.fromRGB(pantColor.hexColor));
//			mystic.setItemMeta(meta);
//
//			NBTItem nbtItem = new NBTItem(mystic);
////			nbtItem.setBoolean(NBTTag.DROP_CONFIRM.getRef(), true);
//			mystic = nbtItem.getItem();
		} else {
			return ItemFactory.getItem(MysticPants.class).getItem(pantColor);

//			mystic = new AItemStackBuilder(Material.LEATHER_LEGGINGS)
//					.setName(pantColor.chatColor + "Fresh " + pantColor.refName + " Pants")
//					.setLore(new ALoreBuilder("&7Kept on death", "&f", "&f",
//							pantColor.chatColor + "Used in the mystic well", pantColor.chatColor + "Also, a fashion statement"))
//					.addUnbreakable(true).getItemStack();
//			mystic = PantColor.setPantColor(mystic, pantColor);
		}

//		NBTItem nbtMystic = new NBTItem(mystic);
//		nbtMystic.setString(NBTTag.ITEM_UUID.getRef(), UUID.randomUUID().toString());
//		nbtMystic.addCompound(NBTTag.PIT_ENCHANTS.getRef());
//		return nbtMystic.getItem();
	}

	public static boolean isFresh(ItemStack itemStack) {
		PitItem pitItem = ItemFactory.getItem(itemStack);
		if(pitItem == null || !pitItem.isMystic) return false;

		NBTItem nbtItem = new NBTItem(itemStack);
		Integer enchantNum = nbtItem.getInteger(NBTTag.ITEM_ENCHANTS.getRef());
		return enchantNum == 0;
	}
}
