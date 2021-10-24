package dev.kyro.pitsim.misc;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.controllers.ItemManager;
import dev.kyro.pitsim.controllers.UpgradeManager;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.inventories.VileGUI;
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

public class ChunkOfVile implements Listener {
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

        vile = ItemManager.enableDropConfirm(vile);

        NBTItem nbtItem  = new NBTItem(vile);
        nbtItem.setBoolean(NBTTag.IS_VILE.getRef(), true);

        AUtil.giveItemSafely(player, nbtItem.getItem(), true);

    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        int items = 0;

        if(Misc.isAirOrNull(event.getPlayer().getItemInHand()) || event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) return;
        NBTItem nbtItem = new NBTItem(event.getPlayer().getItemInHand());

        if(nbtItem.hasKey(NBTTag.IS_VILE.getRef())) {

        if(!UpgradeManager.hasUpgrade(event.getPlayer(), "WITHERCRAFT")) {
            AOutput.error(event.getPlayer(), "&c&lWITHERFAIL! &7You must first unlock Withercraft from the renown shop before using this item!");
            Sounds.ERROR.play(event.getPlayer());
            return;
        }

        for(int i = 0; i < event.getPlayer().getInventory().getSize(); i++) {
            ItemStack item = event.getPlayer().getInventory().getItem(i);

            if(Misc.isAirOrNull(item)) continue;

            NBTItem nbtItem2 = new NBTItem(item);
            if(nbtItem2.hasKey(NBTTag.ITEM_JEWEL_ENCHANT.getRef())) {
                if(nbtItem2.getInteger(NBTTag.CURRENT_LIVES.getRef()).equals(nbtItem2.getInteger(NBTTag.MAX_LIVES.getRef()))) continue;
                items++;
            }

        }

        if(items == 0) {
            AOutput.error(event.getPlayer(), "&c&lWITHERFAIL! &7You have no items to repair!");
            Sounds.ERROR.play(event.getPlayer());
            return;
        }



            VileGUI vileGUI = new VileGUI(event.getPlayer());
            vileGUI.open();
        }
    }
}
