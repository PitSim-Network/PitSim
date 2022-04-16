package dev.kyro.pitsim.brewing.ingredients;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.pitsim.brewing.objects.BrewingIngredient;
import dev.kyro.pitsim.enums.NBTTag;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class RottenFlesh extends BrewingIngredient {
    public static RottenFlesh INSTANCE;

    public RottenFlesh() {
        super(1, NBTTag.ZOMBIE_FLESH);
        INSTANCE = this;
    }

    @Override
    public void administerEffect(Player player, BrewingIngredient potency, BrewingIngredient duration) {

    }

    @Override
    public Object getPotency(BrewingIngredient potencyIngredient) {
        int tier = potencyIngredient.tier;

        return 10 * tier;
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
