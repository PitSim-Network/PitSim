package dev.kyro.pitsim.misc.tainted;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.controllers.ItemManager;
import dev.kyro.pitsim.controllers.UpgradeManager;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.inventories.VileGUI;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class BloodyHeart implements Listener {
//    public static void giveBloodyHeart(Player player, int amount) {
//        ItemStack heart = new ItemStack((short) 351);
//        ItemMeta meta = heart.getItemMeta();
//        meta.setDisplayName(ChatColor.RED + "Bloody Heart");
//        List<String> lore = new ArrayList<>();
//        lore.add(ChatColor.GRAY + "One consume:");
//        lore.add(ChatColor.RED + "Bleed " + ChatColor.GRAY + "every second for 5 seconds");
//        lore.add(ChatColor.GRAY + "losing " + ChatColor.RED + "-10% " + ChatColor.GRAY + "health every second");
//        lore.add(ChatColor.GRAY + "but gain " + ChatColor.RED + "+25% " + ChatColor.GRAY + "damage");
//        lore.add(" ");
//        lore.add(ChatColor.YELLOW + "One time use");
//        meta.setLore(lore);
//        heart.setItemMeta(meta);
//        heart.setAmount(amount);
//
//        heart = ItemManager.enableDropConfirm(heart);
//
//        //NBTItem nbtItem = new NBTItem(heart);
//        //nbtItem.setBoolean(NBTTag.IS_VILE.getRef(), true);
//
//        AUtil.giveItemSafely(player, heart/*nbtItem.getItem()*/, true);
//
//    }
//
//    public static ItemStack getBloodyHeart(int amount) {
//        ItemStack heart = new ItemStack((short) 351);
//        ItemMeta meta = heart.getItemMeta();
//        meta.setDisplayName(ChatColor.RED + "Bloody Heart");
//        List<String> lore = new ArrayList<>();
//        lore.add(ChatColor.GRAY + "One consume:");
//        lore.add(ChatColor.RED + "Bleed " + ChatColor.GRAY + "every second for 5 seconds");
//        lore.add(ChatColor.GRAY + "losing " + ChatColor.RED + "-10% " + ChatColor.GRAY + "health every second");
//        lore.add(ChatColor.GRAY + "but gain " + ChatColor.RED + "+25% " + ChatColor.GRAY + "damage");
//        lore.add(" ");
//        lore.add(ChatColor.YELLOW + "One time use");
//        meta.setLore(lore);
//        heart.setItemMeta(meta);
//        heart.setAmount(amount);
//
//        heart = ItemManager.enableDropConfirm(heart);
//
//        //NBTItem nbtItem = new NBTItem(vile);
//        //nbtItem.setBoolean(NBTTag.IS_VILE.getRef(), true);
//
//        return heart;
//    }
//
//    @EventHandler
//    public void onInteract(PlayerInteractEvent event) {
//        if (event.getItem() != null && event.getItem().getItemMeta() != null) {
//            if (event.getItem().getItemMeta().getDisplayName().equals(ChatColor.RED + "Bloody Heart")) {
//                AOutput.error(event.getPlayer(), "&c&lHEARTFAIL! &7Item not implemented!");
//                Sounds.ERROR.play(event.getPlayer());
//            }
//        }
//    }
}

