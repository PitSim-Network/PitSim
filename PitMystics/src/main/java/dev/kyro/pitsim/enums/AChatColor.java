package dev.kyro.pitsim.enums;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public enum AChatColor {
    DARK_BLUE("Dark Blue", ChatColor.DARK_BLUE, '1', Material.ENDER_PEARL, 0),
    DARK_GREEN("Dark Green", ChatColor.DARK_GREEN, '2', Material.INK_SACK, 2),
    DARK_AQUA("Dark Aqua", ChatColor.DARK_AQUA, '3', Material.INK_SACK, 6),
    DARK_RED("Dark Red", ChatColor.DARK_RED, '4', Material.REDSTONE, 0),
    DARK_PURPLE("Dark Purple", ChatColor.DARK_PURPLE, '5', Material.INK_SACK, 5),
    GOLD("Gold", ChatColor.GOLD, '6', Material.INK_SACK, 14),
    GRAY("Gray", ChatColor.GRAY, '7', Material.INK_SACK, 8),
    BLUE("Blue", ChatColor.BLUE, '9', Material.INK_SACK, 4),
    GREEN("Green", ChatColor.GREEN, 'a', Material.INK_SACK, 10),
    AQUA("Aqua", ChatColor.AQUA, 'b', Material.INK_SACK, 12),
    RED("Red", ChatColor.RED, 'c', Material.INK_SACK, 1),
    LIGHT_PURPLE("Light Purple", ChatColor.LIGHT_PURPLE, 'd', Material.INK_SACK, 13),
    YELLOW("Yellow", ChatColor.YELLOW, 'e', Material.INK_SACK, 11),
    WHITE("White", ChatColor.WHITE, 'f', Material.INK_SACK, 15);



    public String refName;
    public ChatColor chatColor;
    public char code;
    public Material material;
    public int data;

    AChatColor(String refName, ChatColor chatColor, char code, Material material, int data) {
        this.refName = refName;
        this.chatColor = chatColor;
        this.code = code;
        this.material = material;
        this.data = data;
    }
}
