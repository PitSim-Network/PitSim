package dev.kyro.pitsim.brewing.ingredients;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.pitsim.brewing.PotionManager;
import dev.kyro.pitsim.brewing.objects.BrewingIngredient;
import dev.kyro.pitsim.brewing.objects.PotionEffect;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.events.KillEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RottenFlesh extends BrewingIngredient {
    public static RottenFlesh INSTANCE;

    public RottenFlesh() {
        super(1, NBTTag.ZOMBIE_FLESH, "Gold Boost", ChatColor.GOLD, PotionType.FIRE_RESISTANCE);
        INSTANCE = this;
    }

    @Override
    public void administerEffect(Player player, BrewingIngredient potency, int duration) {

    }

    @EventHandler
    public void onKill(KillEvent event) {
        PotionEffect effect = PotionManager.getEffect(event.killer, this);
        if(effect == null) return;

        event.goldMultipliers.add((Double) getPotency(effect.potency) + 1);
    }

    @Override
    public Object getPotency(BrewingIngredient potencyIngredient) {
        int tier = potencyIngredient.tier;

        return 0.1 * tier;
    }

    @Override
    public List<String> getPotencyLore(BrewingIngredient potency) {
        List<String> lore = new ArrayList<>();

        lore.add("");
        lore.add(ChatColor.GRAY + "Earn " + ChatColor.GOLD + "+" + (int) ((double) getPotency(potency) * 100) + "% gold " + ChatColor.GRAY + "from");
        lore.add(ChatColor.GRAY + "kills and assists.");
        return lore;
    }

    @Override
    public int getDuration(BrewingIngredient durationIngredient) {
        int tier = durationIngredient.tier;
        return (20 * 60 * 3) * tier;
    }

    @Override
    public int getBrewingReductionMinutes() {
        return 10;
    }

    @Override
    public ItemStack getItem() {
        ItemStack flesh = new ItemStack(Material.ROTTEN_FLESH);
        ItemMeta meta = flesh.getItemMeta();
        List<String> lore = Arrays.asList(ChatColor.GRAY + "Flesh gathered from the Zombies", ChatColor.GRAY
                + "of the Zombie Caves", "", ChatColor.DARK_PURPLE + "Tainted Item");
        meta.setLore(lore);
        meta.setDisplayName(ChatColor.GREEN + "Rotten Flesh");
        flesh.setItemMeta(meta);

        NBTItem nbtItem = new NBTItem(flesh);
        nbtItem.setBoolean(nbtTag.getRef(), true);
        return nbtItem.getItem();
    }
}
