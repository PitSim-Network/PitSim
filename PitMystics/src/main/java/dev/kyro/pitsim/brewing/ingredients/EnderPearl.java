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

public class EnderPearl extends BrewingIngredient {
    public EnderPearl() {
        super(10, NBTTag.ENDERMAN_PEARL, ChatColor.GREEN + "Venom", ChatColor.GREEN, PotionType.POISON);
    }

    @Override
    public void administerEffect(Player player, BrewingIngredient potency, BrewingIngredient duration) {

    }

    @Override
    public Object getPotency(BrewingIngredient potencyIngredient) {
        return potencyIngredient.tier;
    }

    @Override
    public List<String> getPotencyLore(BrewingIngredient potency) {
        List<String> lore = new ArrayList<>();

        lore.add("");
        lore.add(ChatColor.GRAY + "Disables " + color + getPotencyLore(potency) + " Mystic Tokens" +  ChatColor.GRAY + "from being");
        lore.add(ChatColor.GRAY + "used in any way.");
        return lore;
    }

    @Override
    public int getDuration(BrewingIngredient durationIngredient) {
        return 20 * 15 * durationIngredient.tier;
    }

    @Override
    public int getBrewingReductionMinutes() {
        return 100;
    }

    @Override
    public ItemStack getItem() {
        ItemStack pearl = new ItemStack(Material.ENDER_PEARL);
        ItemMeta meta = pearl.getItemMeta();
        List<String> lore = Arrays.asList(ChatColor.GRAY + "Pearl gathered from the Endermen", ChatColor.GRAY
                + "of the End Caves", "", ChatColor.DARK_PURPLE + "Tainted Item");
        meta.setLore(lore);
        meta.setDisplayName(ChatColor.GREEN + "Ender Pearl");
        pearl.setItemMeta(meta);

        NBTItem nbtItem = new NBTItem(pearl);
        nbtItem.setBoolean(nbtTag.getRef(), true);
        return nbtItem.getItem();
    }
}
