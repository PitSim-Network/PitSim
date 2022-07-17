package dev.kyro.pitsim.enums;

import dev.kyro.pitsim.commands.FreshCommand;
import dev.kyro.pitsim.commands.JewelCommand;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.misc.ChunkOfVile;
import dev.kyro.pitsim.misc.FunkyFeather;
import dev.kyro.pitsim.upgrades.ShardHunter;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

import static dev.kyro.pitsim.misc.tainted.CorruptedFeather.getCorruptedFeather;

public enum ItemType {
    FEATHERS_3(1, getFeathers(3), ChatColor.DARK_AQUA + "3x Funky Feather", 50, 10),
    FEATHERS_5(2, getFeathers(5), ChatColor.DARK_AQUA + "5x Funky Feather", 25, 25),
    VILE_3(3, getVile(3), ChatColor.DARK_PURPLE + "3x Chunk of Vile", 50, 10),
    VILE_5(4, getVile(5), ChatColor.DARK_PURPLE + "5x Chunk of Vile", 25, 25),
    COMP_JEWEL_SWORD(5, FreshCommand.getFreshItem(MysticType.SWORD, PantColor.BLUE), ChatColor.YELLOW + "Completed Hidden Jewel Sword", 25, 25),
    COMP_JEWEL_BOW(6, FreshCommand.getFreshItem(MysticType.BOW, PantColor.BLUE), ChatColor.AQUA + "Completed Hidden Jewel Bow", 25, 25),
    COMP_JEWEL_PANTS(7, FreshCommand.getFreshItem(MysticType.PANTS, PantColor.JEWEL), ChatColor.DARK_AQUA + "Completed Hidden Jewel Pants", 25, 25),
    JEWEL_SWORD(8, JewelCommand.getJewel(MysticType.SWORD, null, 0), ChatColor.YELLOW + "Hidden Jewel Sword", 25, 25),
    JEWEL_BOW(9, JewelCommand.getJewel(MysticType.BOW, null, 0), ChatColor.AQUA + "Hidden Jewel Bow", 25, 25),
    JEWEL_PANTS(10, JewelCommand.getJewel(MysticType.PANTS, null, 0), ChatColor.DARK_AQUA + "Hidden Jewel Pants", 25, 25),
    GEM_SHARD_10(11, ShardHunter.getShardItem(10), ChatColor.GREEN + "10x Ancient Gem Shard", 10, 50),
    GEM_SHARD_25(12, ShardHunter.getShardItem(25), ChatColor.GREEN + "25x Ancient Gem Shard", 5, 100),
    TOTALLY_LEGIT_GEM(13, ShardHunter.getGemItem(), ChatColor.GREEN + "Totally Legit Gem", 1, 250),
    CORRUPTED_FEATHERS_3(14, getCorruptedFeather(3), ChatColor.DARK_PURPLE + "3x Corrupted Feather", 50, 10),
    CORRUPTED_FEATHERS_5(15, getCorruptedFeather(5), ChatColor.DARK_PURPLE + "5x Corrupted Feather", 25, 25);






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

    public static int generateJewelData(ItemStack item) {
        MysticType mysticType = MysticType.getMysticType(item);
        if(mysticType == null) return 0;

        System.out.println(EnchantManager.getEnchants(mysticType).size());
        System.out.println(mysticType);

        return new Random().nextInt(EnchantManager.getEnchants(mysticType).size() - 1);
    }

    public static String jewelDataToEnchant(MysticType mysticType, int data) {
        if(mysticType == null) return null;

        return EnchantManager.getEnchants(mysticType).get(data).refNames.get(0);
    }

    public static ItemStack getJewelItem(int id, int data) {
        MysticType mysticType = null;

        switch(id) {
            case 5:
                mysticType = MysticType.SWORD;
                break;
            case 6:
                mysticType = MysticType.BOW;
                break;
            case 7:
                mysticType = MysticType.PANTS;
                break;
        }

        return JewelCommand.getJewel(mysticType, jewelDataToEnchant(mysticType, data), 0);
    }


    public static ItemStack getFeathers(int amount) {
        return FunkyFeather.getFeather(amount);
    }

    public static ItemStack getVile(int amount) {
        return ChunkOfVile.getVile(amount);
    }




}
