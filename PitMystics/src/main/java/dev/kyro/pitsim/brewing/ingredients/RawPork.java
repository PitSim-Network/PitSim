package dev.kyro.pitsim.brewing.ingredients;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.pitsim.brewing.objects.BrewingIngredient;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.*;

public class RawPork extends BrewingIngredient {
    public Map<Player, Integer> tickMap = new HashMap<>();

    public RawPork() {
        super(7, NBTTag.PIGMAN_PORK, "Regeneration", ChatColor.RED, PotionType.REGEN);
    }

    @Override
    public void administerEffect(Player player, BrewingIngredient potency, int duration) {
        if(duration == 0) tickMap.remove(player);
        else tickMap.putIfAbsent(player, 1);

        for (Map.Entry<Player, Integer> entry : tickMap.entrySet()) {
            if(entry.getValue() - 1 == 0) {
                PitPlayer.getPitPlayer(player).heal((Double) getPotency(potency) * 2);
                tickMap.put(entry.getKey(), 5 * 20);
            } else tickMap.put(entry.getKey(), entry.getValue() - 1);

        }
    }

    @Override
    public Object getPotency(BrewingIngredient potencyIngredient) {
        return 0.5 * potencyIngredient.tier;
    }

    @Override
    public List<String> getPotencyLore(BrewingIngredient potency) {
        List<String> lore = new ArrayList<>();

        lore.add("");
        lore.add(ChatColor.GRAY + "Gain " + color + "+" + getPotency(potency) + "\u2764 " + ChatColor.GRAY + "Every 5 seconds.");
        return lore;
    }

    @Override
    public int getDuration(BrewingIngredient durationIngredient) {
        return 20 * 30 * durationIngredient.tier;
    }

    @Override
    public int getBrewingReductionMinutes() {
        return 70;
    }

    @Override
    public ItemStack getItem() {
        ItemStack pork = new ItemStack(Material.PORK);
        ItemMeta meta = pork.getItemMeta();
        List<String> lore = Arrays.asList(ChatColor.GRAY + "Pork gathered from the Pigmen", ChatColor.GRAY
                + "of the Pigmen Caves", "", ChatColor.DARK_PURPLE + "Tainted Item");
        meta.setLore(lore);
        meta.setDisplayName(ChatColor.GREEN + "Raw Pork");
        pork.setItemMeta(meta);

        NBTItem nbtItem = new NBTItem(pork);
        nbtItem.setBoolean(nbtTag.getRef(), true);
        return nbtItem.getItem();
    }
}
