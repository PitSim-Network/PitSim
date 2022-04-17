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

public class Bone extends BrewingIngredient {
    public static Bone INSTANCE;

    public Bone() {
        super(2, NBTTag.SKELETON_BONE, ChatColor.AQUA + "XP Boost");
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
        return 20;
    }

    @Override
    public ItemStack getItem() {
        ItemStack bone = new ItemStack(Material.BONE);
        ItemMeta meta = bone.getItemMeta();
        List<String> lore = Arrays.asList(ChatColor.GRAY + "Flesh gathered from the Skeletons", ChatColor.GRAY
                + "of the Skeleton Caves", "", ChatColor.DARK_PURPLE + "Tainted Item");
        meta.setLore(lore);
        meta.setDisplayName(ChatColor.GREEN + "Bone");
        bone.setItemMeta(meta);

        NBTItem nbtItem = new NBTItem(bone);
        nbtItem.setBoolean(nbtTag.getRef(), true);
        return nbtItem.getItem();
    }
}
