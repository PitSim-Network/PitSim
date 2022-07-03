package dev.kyro.pitsim.slayers.tainted.Loot;

import dev.kyro.arcticapi.misc.AUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Random;

public class BossDrop {

    Player player;
    LootTable lootTable;

    public BossDrop(Player player, LootTable lootTable) {

        this.player = player;
        this.lootTable = lootTable;

    }

    public void run(){
        double bound = new Random().nextDouble();

        LootRunnable lootRunnable = new LootRunnable(lootTable.toString() + ":DROP:" + ChatColor.stripColor(player.getDisplayName()));

        if(bound < lootTable.chance3){

            AUtil.giveItemSafely(player, lootTable.drop3, true);
            lootRunnable.run(player, lootTable.drop3.getItemMeta().getDisplayName(),"&d&lRNG!");
        }else if(bound < lootTable.chance2){
            AUtil.giveItemSafely(player, lootTable.drop2, true);
            lootRunnable.run(player, lootTable.drop2.getItemMeta().getDisplayName(),"&d&lRNG!");
        }else if (bound < lootTable.chance1){
            AUtil.giveItemSafely(player, lootTable.drop1, true);
            lootRunnable.run(player, lootTable.drop1.getItemMeta().getDisplayName(),"&d&lRNGeSUS!");
        }
    }

}
