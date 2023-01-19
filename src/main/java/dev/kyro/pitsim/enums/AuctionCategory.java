package dev.kyro.pitsim.enums;

import dev.kyro.pitsim.market.SortQuery;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.checkerframework.checker.units.qual.A;

public enum AuctionCategory {
	ALL(Material.GRASS, "All Items", ChatColor.YELLOW),
//	feathers, gems, shards, etc
	PURE_RELATED(Material.CACTUS, "Pure", ChatColor.AQUA),
//	jewels, fresh jewels
	OVERWORLD_GEAR(Material.GOLD_SWORD, "Overworld Gear", ChatColor.DARK_GREEN),
//	scythes, chestplates
	DARKZONE_GEAR(Material.GOLD_HOE, "Darkzone Gear", ChatColor.LIGHT_PURPLE),
//	mob drops, boss drops
	DARKZONE_DROPS(Material.ROTTEN_FLESH, "Darkzone Drops", ChatColor.RED),
//	pretty self-explanatory
	POTIONS(Material.BREWING_STAND_ITEM, "Potions", ChatColor.DARK_PURPLE),
//	prot armor, staff cookies, etc
	MISC(Material.BOOK, "Misc", ChatColor.GOLD);

	public final Material displayMaterial;
	public final String displayName;
	public final ChatColor color;
	AuctionCategory(Material displayMaterial, String displayName, ChatColor color) {
		this.displayMaterial = displayMaterial;
		this.displayName = displayName;
		this.color = color;
	}

	public AuctionCategory getNext() {
		return values()[(ordinal() + 1) % values().length];
	}
}

