package dev.kyro.pitsim.misc;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.enums.NBTTag;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class TokenOfAppreciation {
    public static void giveToken(Player player, int amount) {
        ItemStack vile = new ItemStack(Material.MAGMA_CREAM);
        ItemMeta meta = vile.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Token of Appreciation");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.YELLOW + "Special item");
        lore.add(ChatColor.GRAY + "A token of appreciation for understanding");
        lore.add(ChatColor.GRAY + "why we have to reset the PitSim economy.");
        lore.add(ChatColor.GRAY + "Who knows, this might do something some day.");
        lore.add("");
        String loresMessage  = ChatColor.translateAlternateColorCodes('&',
                "&7To: &8[%luckperms_primary_group_name%&8] %luckperms_prefix%" + player.getName());
        lore.add(PlaceholderAPI.setPlaceholders(player, loresMessage));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7From: &8[&9Dev&8] &9wiji1"));
        meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.setLore(lore);
        vile.setItemMeta(meta);
        vile.setAmount(amount);

        NBTItem nbtItem = new NBTItem(vile);
        nbtItem.setBoolean(NBTTag.IS_TOKEN.getRef(), true);


        AUtil.giveItemSafely(player, nbtItem.getItem());

    }
}
