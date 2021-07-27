package dev.kyro.pitsim.misc;

import dev.kyro.arcticapi.misc.AUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ChunkOfVile {
    public static void giveVile(Player player, int amount) {
        ItemStack vile = new ItemStack(Material.COAL);
        ItemMeta meta = vile.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_PURPLE + "Chunk of Vile");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Kept on death");
        lore.add("");
        lore.add(ChatColor.RED + "Heretic artifact");
        meta.setLore(lore);
        vile.setItemMeta(meta);
        vile.setAmount(amount);

        AUtil.giveItemSafely(player, vile);

    }
}
