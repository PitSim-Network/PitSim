package dev.kyro.pitsim.brewing.ingredients;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.pitsim.brewing.objects.BrewingIngredient;
import dev.kyro.pitsim.enums.NBTTag;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FermentedSpiderEye extends BrewingIngredient {
    public FermentedSpiderEye() {
        super(5, NBTTag.CAVESPIDER_EYE, ChatColor.YELLOW + "Neutrality", ChatColor.YELLOW, PotionType.REGEN);
    }

    @Override
    public void administerEffect(Player player, BrewingIngredient potency, BrewingIngredient duration) {

    }

    @Override
    public Object getPotency(BrewingIngredient potencyIngredient) {
        return 0.1 * potencyIngredient.tier;
    }

    @Override
    public List<String> getPotencyLore(BrewingIngredient potency) {
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.WHITE + "" + (int) ((double) getPotency(potency) * 100) + "% " + ChatColor.GRAY + "chance to " + color + "deflect " + ChatColor.GRAY + " incoming hits");
        lore.add(ChatColor.GRAY + "and " + color + "cancel " + ChatColor.GRAY + "out going hits.");
        return lore;
    }

    @Override
    public int getDuration(BrewingIngredient durationIngredient) {
        return 0;
    }

    @Override
    public int getBrewingReductionMinutes() {
        return 50;
    }

    @Override
    public ItemStack getItem() {
        ItemStack eye = new ItemStack(Material.FERMENTED_SPIDER_EYE);
        ItemMeta meta = eye.getItemMeta();
        List<String> lore = Arrays.asList(ChatColor.GRAY + "Fermented Eye gathered from the Spiders", ChatColor.GRAY
                + "of the deeper Spider Cave", "", ChatColor.DARK_PURPLE + "Tainted Item");
        meta.setLore(lore);
        meta.setDisplayName(ChatColor.GREEN + "Fermented Spider Eye");
        eye.setItemMeta(meta);

        NBTItem nbtItem = new NBTItem(eye);
        nbtItem.setBoolean(nbtTag.getRef(), true);
        return nbtItem.getItem();
    }
}
