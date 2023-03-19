package dev.kyro.pitsim.enums;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.pitsim.aitems.mystics.MysticPants;
import dev.kyro.pitsim.controllers.ItemFactory;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public enum PantColor {

	RED("Red", ChatColor.RED, 0xFF5555),
	ORANGE("Orange", ChatColor.GOLD, 0xFFAA00),
	YELLOW("Yellow", ChatColor.YELLOW, 0xFFFF55),
	GREEN("Green", ChatColor.GREEN, 0x55FF55),
	BLUE("Blue", ChatColor.BLUE, 0x5555FF),

	//Premium Colors
	PEACE_WHITE("Peace White", ChatColor.WHITE, 0xFFFFFF),
	BUSINESS_GRAY("Business Gray", ChatColor.GRAY, 0xA9A9A9),
	PURE_RED("Pure Red", ChatColor.RED, 0xFF0000),
	BLOOD_RED("Blood Red", ChatColor.DARK_RED, 0x660000),
	SOLID_TEAL("Solid Teal", ChatColor.DARK_AQUA, 0x8080),
	DEEP_SKY("Deep Sky", ChatColor.AQUA, 0xBFFF),
	DARK_ORCHID("Dark Orchid", ChatColor.DARK_PURPLE, 0x9932CC),
	SMOOTH_CRIMSON("Smooth Crimson", ChatColor.DARK_PURPLE, 0xC6143A),
	HOT_PINK("Hot Pink", ChatColor.LIGHT_PURPLE, 0xFF00FF),
	UGLY_CLASSIC("Ugly Classic", ChatColor.WHITE, 0xA06540),
	SHADOW_GRAY("Shadow Gray", ChatColor.DARK_GRAY, 0x606060),
	NIGHT_RIDER("Night Rider", ChatColor.DARK_PURPLE, 0x400060),
	LIGHT_LAVENDER("Light Lavender", ChatColor.LIGHT_PURPLE, 0xDDAADD),
	BLUEBERRY_BLUES("Blueberry Blues", ChatColor.BLUE, 0x555599),
	SOOTHING_BEIGE("Soothing Beige", ChatColor.YELLOW, 0xFFDDAA),
	NOT_ORANGE("Not Orange", ChatColor.GOLD, 0xFFBB00),
	NEON_GREEN("Neon Green", ChatColor.GREEN, 0xAAE100),
	HARVEST_RED("Harvest Red", ChatColor.RED, 0xEE3300),

	//Special Colors
	DARK("Dark", ChatColor.DARK_PURPLE, 0x000000),
	TAINTED("", ChatColor.DARK_PURPLE, 0x7100e3),
	JEWEL("Jewel", ChatColor.DARK_AQUA, 0x7DC383);
//	AQUA("", ChatColor.DARK_AQUA, 0x55FFFF);

	public final String displayName;
	public final ChatColor chatColor;
	public final int hexColor;

	PantColor(String displayName, ChatColor chatColor, int hexColor) {
		this.displayName = displayName;
		this.chatColor = chatColor;
		this.hexColor = hexColor;
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
		leatherArmorMeta.setDisplayName(pantColor.chatColor + (ChatColor.RESET + leatherArmorMeta.getDisplayName()));
		itemStack.setItemMeta(leatherArmorMeta);
	}
}
