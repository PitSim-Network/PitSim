package dev.kyro.pitsim.brewing.ingredients;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.pitsim.brewing.objects.BrewingIngredient;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.*;

public class Coal extends BrewingIngredient {
    public static Coal INSTANCE;
    public Map<Player, Integer> tickMap = new HashMap<>();

    public Coal() {
        super(8, NBTTag.WITHER_SKELETON_COAL, "Wither", ChatColor.DARK_GRAY, PotionType.WEAKNESS);
        INSTANCE = this;
    }

    @Override
    public void administerEffect(Player player, BrewingIngredient potency, int duration) {
        Misc.applyPotionEffect(player, PotionEffectType.WITHER, duration, 0, false, false);
        if(duration == 0) tickMap.remove(player);
        else tickMap.putIfAbsent(player, 1);

        for (Map.Entry<Player, Integer> entry : tickMap.entrySet()) {
            if(entry.getValue() - 1 == 0) {
                PitPlayer.getPitPlayer(player).damage((double) getPotency(potency) * 2, null);
                tickMap.put(entry.getKey(), 5 * 20);
            } else tickMap.put(entry.getKey(), entry.getValue() - 1);

        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if(!(event.getEntity() instanceof Player)) return;
        if(!tickMap.containsKey((Player) event.getEntity())) return;

        if(event.getCause() == EntityDamageEvent.DamageCause.WITHER) event.setCancelled(true);
    }

    @Override
    public Object getPotency(BrewingIngredient potencyIngredient) {
        return 0.5 * potencyIngredient.tier;
    }

    @Override
    public List<String> getPotencyLore(BrewingIngredient potency) {
        List<String> lore = new ArrayList<>();

        lore.add("");
        lore.add(ChatColor.GRAY + "Lose " + color + "" + getPotency(potency) + "\u2764 " +  ChatColor.GRAY + "Every 5 seconds.");
        return lore;
    }

    @Override
    public int getDuration(BrewingIngredient durationIngredient) {
        return 20 * durationIngredient.tier;
    }

    @Override
    public int getBrewingReductionMinutes() {
        return 80;
    }

    @Override
    public ItemStack getItem() {
        ItemStack skull = new ItemStack(Material.COAL);
        ItemMeta meta = skull.getItemMeta();
        List<String> lore = Arrays.asList(ChatColor.GRAY + "Coal gathered from the Skeletons", ChatColor.GRAY
                + "of the Wither Caves", "", ChatColor.DARK_PURPLE + "Tainted Item");
        meta.setLore(lore);
        meta.setDisplayName(ChatColor.GREEN + "Coal");
        skull.setItemMeta(meta);

        NBTItem nbtItem = new NBTItem(skull);
        nbtItem.setBoolean(nbtTag.getRef(), true);
        return nbtItem.getItem();
    }
}
