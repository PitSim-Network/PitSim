package dev.kyro.pitsim.brewing.ingredients;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.pitsim.brewing.PotionManager;
import dev.kyro.pitsim.brewing.objects.BrewingIngredient;
import dev.kyro.pitsim.brewing.objects.PotionEffect;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.events.AttackEvent;
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

public class IronIngot extends BrewingIngredient {
    public IronIngot() {
        super(9, NBTTag.GOLEM_INGOT, "Defense", ChatColor.BLUE, PotionType.NIGHT_VISION);
    }

    @Override
    public void administerEffect(Player player, BrewingIngredient potency, int duration) {

    }

    @EventHandler
    public void onHit(AttackEvent.Apply event) {
        PotionEffect effect = PotionManager.getEffect(event.defenderPlayer, this);
        if(effect == null) return;

        double defense = (double) getPotency(effect.potency);
        event.multipliers.add(defense);
    }

    @Override
    public Object getPotency(BrewingIngredient potencyIngredient) {
        return 0.1 * potencyIngredient.tier;
    }

    @Override
    public List<String> getPotencyLore(BrewingIngredient potency) {
        List<String> lore = new ArrayList<>();

        lore.add("");
        lore.add(ChatColor.GRAY + "Receive " + color + "-" + (int) ((double)getPotency(potency) * 100) + "% Damage" +  ChatColor.GRAY + ".");
        return lore;
    }

    @Override
    public int getDuration(BrewingIngredient durationIngredient) {
        return 20 * 30 * durationIngredient.tier;
    }

    @Override
    public int getBrewingReductionMinutes() {
        return 90;
    }

    @Override
    public ItemStack getItem() {
        ItemStack ingot = new ItemStack(Material.IRON_INGOT);
        ItemMeta meta = ingot.getItemMeta();
        List<String> lore = Arrays.asList(ChatColor.GRAY + "Ingot gathered from the Golems", ChatColor.GRAY
                + "of the Golem Caves", "", ChatColor.DARK_PURPLE + "Tainted Item");
        meta.setLore(lore);
        meta.setDisplayName(ChatColor.GREEN + "Iron Ingot");
        ingot.setItemMeta(meta);

        NBTItem nbtItem = new NBTItem(ingot);
        nbtItem.setBoolean(nbtTag.getRef(), true);
        return nbtItem.getItem();
    }
}
