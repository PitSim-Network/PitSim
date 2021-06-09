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
//	DARK("", ChatColor.DARK_PURPLE, 0x000000),
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
		itemStack.setItemMeta(leatherArmorMeta);
	}
}
