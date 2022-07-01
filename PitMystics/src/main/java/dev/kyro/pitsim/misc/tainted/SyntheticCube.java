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

public class SyntheticCube implements Listener {
    public static void giveSyntheticCube(Player player, int amount) {
        ItemStack cube = new ItemStack(Material.COAL);
        ItemMeta meta = cube.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "Synthetic Cube");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "When applied to a " + ChatColor.DARK_PURPLE + "Tainted Scythe");
        lore.add(ChatColor.GRAY + "it will trigger a " + ChatColor.YELLOW + "chemical " + ChatColor.GRAY + "reaction");
        lore.add(ChatColor.GRAY + "causing it's " + ChatColor.GOLD + "molecular " + ChatColor.GRAY + "structure");
        lore.add(ChatColor.GRAY + "to reform into a " + ChatColor.AQUA + "diamond");
        meta.setLore(lore);
        cube.setItemMeta(meta);
        cube.setAmount(amount);

        cube = ItemManager.enableDropConfirm(cube);

        //NBTItem nbtItem = new NBTItem(cube);
        //nbtItem.setBoolean(NBTTag.IS_VILE.getRef(), true);

        AUtil.giveItemSafely(player, cube/*nbtItem.getItem()*/, true);

    }

    public static ItemStack getSyntheticCube(int amount) {
        ItemStack cube = new ItemStack(Material.DIAMOND_BLOCK);
        ItemMeta meta = cube.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "Synthetic Cube");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "When applied to a " + ChatColor.DARK_PURPLE + "Tainted Scythe");
        lore.add(ChatColor.GRAY + "it will trigger a " + ChatColor.YELLOW + "chemical " + ChatColor.GRAY + "reaction");
        lore.add(ChatColor.GRAY + "causing it's " + ChatColor.GOLD + "molecular " + ChatColor.GRAY + "structure");
        lore.add(ChatColor.GRAY + "to reform into a " + ChatColor.AQUA + "diamond");
        meta.setLore(lore);
        cube.setItemMeta(meta);
        cube.setAmount(amount);

        cube = ItemManager.enableDropConfirm(cube);

        //NBTItem nbtItem = new NBTItem(cube);
        //nbtItem.setBoolean(NBTTag.IS_VILE.getRef(), true);

        return cube;//nbtItem.getItem();
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if(event.getItem() != null && event.getItem().getItemMeta() != null){
            if(event.getItem().getItemMeta().getDisplayName().equals(ChatColor.AQUA + "Synthetic Cube")){
                AOutput.error(event.getPlayer(), "&c&lCUBEFAIL! &7Item not implemented!");
                Sounds.ERROR.play(event.getPlayer());
            }
        }

    }
}

