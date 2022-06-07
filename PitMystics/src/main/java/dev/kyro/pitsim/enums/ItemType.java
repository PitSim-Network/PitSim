package dev.kyro.pitsim.enums;

import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.misc.ChunkOfVile;
import dev.kyro.pitsim.misc.FunkyFeather;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public enum ItemType {

    FEATHERS_3(1, getFeathers(3), 75, 5),
    FEATHERS_5(2, getFeathers(5), 25, 10),
    VILE_3(3, getVile(3), 75, 5),
    VILE_5(4, getVile(5), 25, 10);




    public final int id;
    public final ItemStack item;
    public final double chance;
    public final int startingBid;

    ItemType(int id, ItemStack item, double chance, int startingBid) {
        this.id = id;
        this.item = item;
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

        return new Random().nextInt(EnchantManager.getEnchants(mysticType).size() - 1);
    }

    public static PitEnchant jewelDataToEnchant(ItemStack item, int data) {
        MysticType mysticType = MysticType.getMysticType(item);
        if(mysticType == null) return null;

        return EnchantManager.getEnchants(mysticType).get(data);
    }


    public static ItemStack getFeathers(int amount) {
        return FunkyFeather.getFeather(amount);
    }

    public static ItemStack getVile(int amount) {
        return ChunkOfVile.getVile(amount);
    }




}
