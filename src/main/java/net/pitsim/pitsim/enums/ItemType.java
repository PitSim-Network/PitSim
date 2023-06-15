package net.pitsim.pitsim.enums;

import net.pitsim.pitsim.aitems.MysticFactory;
import net.pitsim.pitsim.aitems.misc.AncientGemShard;
import net.pitsim.pitsim.aitems.misc.ChunkOfVile;
import net.pitsim.pitsim.aitems.misc.CorruptedFeather;
import net.pitsim.pitsim.aitems.misc.FunkyFeather;
import net.pitsim.pitsim.commands.admin.JewelCommand;
import net.pitsim.pitsim.controllers.EnchantManager;
import net.pitsim.pitsim.controllers.ItemFactory;
import net.pitsim.pitsim.misc.Misc;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public enum ItemType {
	FEATHERS_3(1, ItemFactory.getItem(FunkyFeather.class).getItem(3), ChatColor.DARK_AQUA + "3x Funky Feather", 50, 10),
	FEATHERS_5(2, ItemFactory.getItem(FunkyFeather.class).getItem(5), ChatColor.DARK_AQUA + "5x Funky Feather", 25, 25),
	VILE_3(3, ItemFactory.getItem(ChunkOfVile.class).getItem(3), ChatColor.DARK_PURPLE + "3x Chunk of Vile", 50, 10),
	VILE_5(4, ItemFactory.getItem(ChunkOfVile.class).getItem(5), ChatColor.DARK_PURPLE + "5x Chunk of Vile", 25, 25),
	COMP_JEWEL_SWORD(5, MysticFactory.getFreshItem(MysticType.SWORD, PantColor.BLUE), ChatColor.YELLOW + "Completed Hidden Jewel Sword", 25, 25),
	COMP_JEWEL_BOW(6, MysticFactory.getFreshItem(MysticType.BOW, PantColor.BLUE), ChatColor.AQUA + "Completed Hidden Jewel Bow", 25, 25),
	COMP_JEWEL_PANTS(7, MysticFactory.getFreshItem(MysticType.PANTS, PantColor.JEWEL), ChatColor.DARK_AQUA + "Completed Hidden Jewel Pants", 25, 25),
	JEWEL_SWORD(8, JewelCommand.getJewel(MysticType.SWORD, null, 0), ChatColor.YELLOW + "Hidden Jewel Sword", 25, 25),
	JEWEL_BOW(9, JewelCommand.getJewel(MysticType.BOW, null, 0), ChatColor.AQUA + "Hidden Jewel Bow", 25, 25),
	JEWEL_PANTS(10, JewelCommand.getJewel(MysticType.PANTS, null, 0), ChatColor.DARK_AQUA + "Hidden Jewel Pants", 25, 25),
	GEM_SHARD_10(11, ItemFactory.getItem(AncientGemShard.class).getItem(5), ChatColor.GREEN + "5x Ancient Gem Shard", 10, 50),
	GEM_SHARD_25(12, ItemFactory.getItem(AncientGemShard.class).getItem(10), ChatColor.GREEN + "10x Ancient Gem Shard", 5, 100),
	CORRUPTED_FEATHERS_3(13, ItemFactory.getItem(CorruptedFeather.class).getItem(3), ChatColor.DARK_PURPLE + "3x Corrupted Feather", 50, 10),
	CORRUPTED_FEATHERS_5(14, ItemFactory.getItem(CorruptedFeather.class).getItem(5), ChatColor.DARK_PURPLE + "5x Corrupted Feather", 25, 25);

	public final int id;
	public final ItemStack item;
	public final String itemName;
	public final double chance;
	public final int startingBid;

	ItemType(int id, ItemStack item, String itemName, double chance, int startingBid) {
		this.id = id;
		this.item = item;
		this.itemName = itemName;
		this.chance = chance;
		this.startingBid = startingBid;
	}

	public static ItemType getItemType(int id) {
		for(ItemType itemType : values()) {
			if(itemType.id == id) return itemType;
		}
		return null;
	}

	public static int getJewelData(ItemStack item, long seed) {
		MysticType mysticType = MysticType.getMysticType(item);
		if(mysticType == null) return 0;

		Random random = new Random(seed);
		return random.nextInt(EnchantManager.getEnchants(mysticType).size() - 1);
	}

	public static ItemType getItem(long seed) {
		Random random = new Random(seed);

		int totalPercentChance = 0;
		for (ItemType itemType : values()) {
			totalPercentChance += itemType.chance;
		}

		int randomInt = random.nextInt(totalPercentChance) + 1;

		for(ItemType value : values()) {
			randomInt -= value.chance;
			if(randomInt <= 0) return value;
		}

		return null;
	}

	public static String getEnchantFromJewelData(MysticType mysticType, int data) {
		if(mysticType == null) return null;

		return EnchantManager.getEnchants(mysticType).get(data).refNames.get(0);
	}

	public static ItemStack getJewelItem(int id, int data) {
		MysticType mysticType = getMysticTypeID(id);
		if(mysticType == null) {
			Misc.alertDiscord("@everyone Invalid jewel id: " + id);
			throw new RuntimeException("Invalid jewel id: " + id);
		}

		return JewelCommand.getJewel(mysticType, getEnchantFromJewelData(mysticType, data), 0);
	}

	public static MysticType getMysticTypeID(int id) {
		switch(id) {
			case 5:
				return MysticType.SWORD;
			case 6:
				return MysticType.BOW;
			case 7:
				return MysticType.PANTS;
			default:
				return null;
		}
	}

}
