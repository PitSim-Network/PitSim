package dev.kyro.pitsim.slayers.tainted.Loot;

import org.bukkit.inventory.ItemStack;

public enum LootTable {
    ZOMBIE_CAVE(null,null,null,
    .35,.20,.15),
    SKELETON_CAVE(null,null,null,
            .35,.20,.15),
    SPIDER_CAVE(null,null,null,
            .35,.20,.15),
    CREEPER_CAVE(null,null,null,
            .35,.20,.15),
    DEEP_SPIDER_CAVE(null,null,null,
            .35,.20,.15),
    PIGMEN_CAVE(null,null,null,
            .35,.20,.15),
    MAGMA_CAVE(null,null,null,
            .35,.20,.15),
    WITHER_CAVE(null,null,null,
            .35,.20,.15),
    GOLEM_CAVE(null,null,null,
            .35,.20,.15),
    ENDERMAN_CAVE(null,null,null,
            .35,.20,.15);


    public ItemStack drop1;
    public ItemStack drop2;
    public ItemStack drop3;
    public double chance1;
    public double chance2;
    public double chance3;

    LootTable(ItemStack drop1, ItemStack drop2, ItemStack drop3, double chance1, double chance2, double chance3) {
        this.chance1 = chance1;
        this.chance2 = chance2;
        this.chance3 = chance3;

        this.drop1 = drop1;
        this.drop2 = drop2;
        this.drop3 = drop3;
    }

}
