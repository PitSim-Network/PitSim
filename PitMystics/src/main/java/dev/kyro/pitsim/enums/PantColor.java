package dev.kyro.pitsim.enums;

import dev.kyro.pitsim.misc.Misc;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
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
	JEWEL("Jewel", ChatColor.DARK_AQUA, 0x7DC383);
//	AQUA("", ChatColor.DARK_AQUA, 0x55FFFF);

	public String refName;
	public ChatColor chatColor;
	public int hexColor;

	PantColor(String refName, ChatColor chatColor, int hexColor) {
		this.refName = refName;
		this.chatColor = chatColor;
		this.hexColor = hexColor;
	}

	public static PantColor getNormalRandom() {

		return values()[(int) (Math.random() * 5)];
	}

	public static PantColor getPantColor(String refName) {

		for(PantColor pantColor : values()) {

			if(pantColor.refName.equalsIgnoreCase(refName)) return pantColor;
		}
		return null;
	}

	public static PantColor getPantColor(ItemStack itemStack) {

		if(Misc.isAirOrNull(itemStack) || itemStack.getType() != Material.LEATHER_LEGGINGS) return null;
		LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) itemStack.getItemMeta();

		for(PantColor pantColor : values()) {

			if(Color.fromRGB(pantColor.hexColor).equals(leatherArmorMeta.getColor())) return pantColor;
		}

		return null;
	}

	public static void setPantColor(ItemStack itemStack, PantColor pantColor) {
		if(Misc.isAirOrNull(itemStack) || itemStack.getType() != Material.LEATHER_LEGGINGS) return;
		LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) itemStack.getItemMeta();

		leatherArmorMeta.setColor(Color.fromRGB(pantColor.hexColor));
		
		leatherArmorMeta.setDisplayName(pantColor.chatColor + (ChatColor.RESET + leatherArmorMeta.getDisplayName()));
		itemStack.setItemMeta(leatherArmorMeta);
	}
}
