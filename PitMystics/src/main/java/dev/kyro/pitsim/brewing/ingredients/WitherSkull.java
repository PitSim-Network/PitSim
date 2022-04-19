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

public class WitherSkull extends BrewingIngredient {
    public WitherSkull() {
        super(8, NBTTag.WITHER_SKULL, ChatColor.BLUE + "Wither", ChatColor.BLACK, PotionType.WEAKNESS);
    }

    @Override
    public void administerEffect(Player player, BrewingIngredient potency, BrewingIngredient duration) {

    }

    @Override
    public Object getPotency(BrewingIngredient potencyIngredient) {
        return 0.5 * potencyIngredient.tier;
    }

    @Override
    public List<String> getPotencyLore(BrewingIngredient potency) {
        List<String> lore = new ArrayList<>();

        lore.add("");
        lore.add(ChatColor.GRAY + "Lose " + color + "" + getPotency(potency) + "\u2764" +  ChatColor.GRAY + "Every 5 seconds.");
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
        ItemStack skull = new ItemStack(Material.SKULL);
        ItemMeta meta = skull.getItemMeta();
        List<String> lore = Arrays.asList(ChatColor.GRAY + "Skull gathered from the Skeletons", ChatColor.GRAY
                + "of the Wither Caves", "", ChatColor.DARK_PURPLE + "Tainted Item");
        meta.setLore(lore);
        meta.setDisplayName(ChatColor.GREEN + "Wither Skeleton Skull");
        skull.setItemMeta(meta);

        NBTItem nbtItem = new NBTItem(skull);
        nbtItem.setBoolean(nbtTag.getRef(), true);
        return nbtItem.getItem();
    }
}
