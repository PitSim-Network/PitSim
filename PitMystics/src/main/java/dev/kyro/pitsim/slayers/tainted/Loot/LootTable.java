package dev.kyro.pitsim.slayers.tainted.Loot;

import dev.kyro.pitsim.misc.tainted.BloodyHeart;
import dev.kyro.pitsim.misc.tainted.CorruptedFeather;
import dev.kyro.pitsim.misc.tainted.SyntheticCube;
import org.bukkit.inventory.ItemStack;

public enum LootTable {
    ZOMBIE_CAVE(CorruptedFeather.getCorruptedFeather(1), null, null,
    .01,0,0),
    SKELETON_CAVE(CorruptedFeather.getCorruptedFeather(1), null, null,
    .02,0,0),
    SPIDER_CAVE(CorruptedFeather.getCorruptedFeather(1), null, null,
            .03,0,0),
    CREEPER_CAVE(CorruptedFeather.getCorruptedFeather(1), null, null,
            .04,0,0),
    DEEP_SPIDER_CAVE(CorruptedFeather.getCorruptedFeather(1), null, null,
            .05,0,0),
    PIGMEN_CAVE(CorruptedFeather.getCorruptedFeather(1), null, null,
            .08,0,0),
    MAGMA_CAVE(CorruptedFeather.getCorruptedFeather(1), null, null,
            .11,0,0),
    WITHER_CAVE(CorruptedFeather.getCorruptedFeather(1), null, null,
            .14,0,0),
    GOLEM_CAVE(CorruptedFeather.getCorruptedFeather(1), null, null,
            .17,0,0),
    ENDERMAN_CAVE(CorruptedFeather.getCorruptedFeather(1), null, null,
            .20,0,0);


    public final ItemStack drop1;
    public final ItemStack drop2;
    public final ItemStack drop3;
    public final double chance1;
    public final double chance2;
    public final double chance3;

    LootTable(ItemStack drop1, ItemStack drop2, ItemStack drop3, double chance1, double chance2, double chance3) {
        this.chance1 = chance1;
        this.chance2 = chance2;
        this.chance3 = chance3;

        this.drop1 = drop1;
        this.drop2 = drop2;
        this.drop3 = drop3;
    }

}
