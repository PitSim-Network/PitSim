package dev.kyro.pitsim.enums;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.pitsim.aitems.MysticFactory;
import dev.kyro.pitsim.aitems.mystics.MysticPants;
import dev.kyro.pitsim.controllers.ItemFactory;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public enum PantColor {

	RED("Red", ChatColor.RED, 0xFF5555, true),
	ORANGE("Orange", ChatColor.GOLD, 0xFFAA00, true),
	YELLOW("Yellow", ChatColor.YELLOW, 0xFFFF55, true),
	GREEN("Green", ChatColor.GREEN, 0x55FF55, true),
	BLUE("Blue", ChatColor.BLUE, 0x5555FF, true),

	//Premium Colors
	PEACE_WHITE("Peace White", ChatColor.WHITE, 0xFFFFFF, false),
	BUSINESS_GRAY("Business Gray", ChatColor.GRAY, 0xA9A9A9, false),
	PURE_RED("Pure Red", ChatColor.RED, 0xFF0000, false),
	BLOOD_RED("Blood Red", ChatColor.DARK_RED, 0x660000, false),
	SOLID_TEAL("Solid Teal", ChatColor.DARK_AQUA, 0x8080, false),
	DEEP_SKY("Deep Sky", ChatColor.AQUA, 0xBFFF, false),
	DARK_ORCHID("Dark Orchid", ChatColor.DARK_PURPLE, 0x9932CC, false),
	SMOOTH_CRIMSON("Smooth Crimson", ChatColor.DARK_PURPLE, 0xC6143A, false),
	HOT_PINK("Hot Pink", ChatColor.LIGHT_PURPLE, 0xFF00FF, false),
	UGLY_CLASSIC("Ugly Classic", ChatColor.WHITE, 0xA06540, false),
	SHADOW_GRAY("Shadow Gray", ChatColor.DARK_GRAY, 0x606060, false),
	NIGHT_RIDER("Night Rider", ChatColor.DARK_PURPLE, 0x400060, false),
	LIGHT_LAVENDER("Light Lavender", ChatColor.LIGHT_PURPLE, 0xDDAADD, false),
	BLUEBERRY_BLUES("Blueberry Blues", ChatColor.BLUE, 0x555599, false),
	SOOTHING_BEIGE("Soothing Beige", ChatColor.YELLOW, 0xFFDDAA, false),
	NOT_ORANGE("Not Orange", ChatColor.GOLD, 0xFFBB00, false),
	NEON_GREEN("Neon Green", ChatColor.GREEN, 0xAAE100, false),
	HARVEST_RED("Harvest Red", ChatColor.RED, 0xEE3300, false),

	//Special Colors
	DARK("Dark", ChatColor.DARK_PURPLE, 0x000000, false),
	TAINTED("Tainted", ChatColor.DARK_PURPLE, 0x7100e3, false),
	JEWEL("Jewel", ChatColor.DARK_AQUA, 0x7DC383, false);
//	AQUA("", ChatColor.DARK_AQUA, 0x55FFFF);

	public final String displayName;
	public final ChatColor chatColor;
	public final int hexColor;
	public final boolean isDefault;

	PantColor(String displayName, ChatColor chatColor, int hexColor, boolean isDefault) {
		this.displayName = displayName;
		this.chatColor = chatColor;
		this.hexColor = hexColor;
		this.isDefault = isDefault;
	}

	public static PantColor getNormalRandom() {
		return values()[(int) (Math.random() * 5)];
	}

	public static PantColor getPantColor(String refName) {
		for(PantColor pantColor : values()) if(pantColor.displayName.equalsIgnoreCase(refName)) return pantColor;
		return null;
	}

	public static PantColor getPantColor(ItemStack itemStack) {
		if(!(ItemFactory.getItem(itemStack) instanceof MysticPants)) return null;
		NBTItem nbtItem = new NBTItem(itemStack);
		return PantColor.getPantColor(nbtItem.getString(NBTTag.SAVED_PANTS_COLOR.getRef()));
	}

	public static ItemStack setPantColor(ItemStack itemStack, PantColor pantColor) {
		if(!(ItemFactory.getItem(itemStack) instanceof MysticPants)) return itemStack;

		if(itemStack.getItemMeta() instanceof LeatherArmorMeta) {
			LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) itemStack.getItemMeta();
			leatherArmorMeta.setColor(Color.fromRGB(pantColor.hexColor));
			leatherArmorMeta.setDisplayName(pantColor.chatColor + (ChatColor.RESET + leatherArmorMeta.getDisplayName()));
			itemStack.setItemMeta(leatherArmorMeta);
		}

		NBTItem nbtItem = new NBTItem(itemStack, true);
		nbtItem.setString(NBTTag.SAVED_PANTS_COLOR.getRef(), pantColor.displayName);

		updatePantColor(itemStack);
		return itemStack;
	}

	public static void updatePantColor(ItemStack itemStack) {
		if(!(ItemFactory.getItem(itemStack) instanceof MysticPants) ||
				!(itemStack.getItemMeta() instanceof LeatherArmorMeta)) return;

		PantColor pantColor = getPantColor(itemStack);
		if(pantColor == null) return;

		LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) itemStack.getItemMeta();
		leatherArmorMeta.setColor(Color.fromRGB(pantColor.hexColor));
		if(MysticFactory.isFresh(itemStack)) leatherArmorMeta.setDisplayName(pantColor.chatColor + ChatColor.stripColor(leatherArmorMeta.getDisplayName()));
		itemStack.setItemMeta(leatherArmorMeta);
	}
}
