package dev.kyro.pitsim.brewing.ingredients;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.pitsim.brewing.objects.BrewingIngredient;
import dev.kyro.pitsim.enums.NBTTag;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MagmaCream extends BrewingIngredient {
    public MagmaCream() {
        super(6, NBTTag.MAGMACUBE_CREAM, ChatColor.LIGHT_PURPLE + "Mana Boost", ChatColor.LIGHT_PURPLE, PotionType.INSTANT_HEAL);
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
        lore.add(ChatColor.GRAY + "Regain your " + color + "Mana " + (int) ((double) getPotency(potency) * 100) + "% " + ChatColor.GRAY + "faster");
        lore.add(ChatColor.GRAY + "While in the " + ChatColor.DARK_PURPLE + "Darkzone" + ChatColor.GRAY + ".");
        return lore;
    }

    @Override
    public int getDuration(BrewingIngredient durationIngredient) {
        return 20 * 60 * durationIngredient.tier;
    }

    @Override
    public int getBrewingReductionMinutes() {
        return 60;
    }

    @Override
    public ItemStack getItem() {
        ItemStack cream = new ItemStack(Material.MAGMA_CREAM);
        ItemMeta meta = cream.getItemMeta();
        List<String> lore = Arrays.asList(ChatColor.GRAY + "Cream gathered from the Cubes", ChatColor.GRAY
                + "of the Magma Caves", "", ChatColor.DARK_PURPLE + "Tainted Item");
        meta.setLore(lore);
        meta.setDisplayName(ChatColor.GREEN + "Magma Cream");
        cream.setItemMeta(meta);

        NBTItem nbtItem = new NBTItem(cream);
        nbtItem.setBoolean(nbtTag.getRef(), true);
        return nbtItem.getItem();
    }
}
