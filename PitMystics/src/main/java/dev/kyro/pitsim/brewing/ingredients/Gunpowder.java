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

public class Gunpowder extends BrewingIngredient {
    public static Gunpowder INSTANCE;
    public Gunpowder() {
        super(4, NBTTag.CREEPER_POWDER, "Damage Boost", ChatColor.RED, PotionType.STRENGTH);
        INSTANCE = this;
    }

    @Override
    public void administerEffect(Player player, BrewingIngredient potency, int duration) {

    }

    @EventHandler
    public void onHit(AttackEvent.Apply event) {
        PotionEffect effect = PotionManager.getEffect(event.attackerPlayer, this);
        if(effect == null) return;

        double dmg = (double) getPotency(effect.potency);
        event.increasePercent += dmg;
    }

    @Override
    public Object getPotency(BrewingIngredient potencyIngredient) {
        return 0.05 * potencyIngredient.tier;
    }

    @Override
    public List<String> getPotencyLore(BrewingIngredient potency) {
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GRAY + "Deal " + color + "+" + (int) ((double) getPotency(potency) * 100) + "% Damage");
        lore.add(ChatColor.GRAY + "to bots and players.");
        return lore;
    }

    @Override
    public int getDuration(BrewingIngredient durationIngredient) {
        return 20 * 60 * durationIngredient.tier;
    }

    @Override
    public int getBrewingReductionMinutes() {
        return 40;
    }

    @Override
    public ItemStack getItem() {
        ItemStack powder = new ItemStack(Material.SULPHUR);
        ItemMeta meta = powder.getItemMeta();
        List<String> lore = Arrays.asList(ChatColor.GRAY + "Gunpowder gathered from the Creepers", ChatColor.GRAY
                + "of the Creeper Caves", "", ChatColor.DARK_PURPLE + "Tainted Item");
        meta.setLore(lore);
        meta.setDisplayName(ChatColor.GREEN + "Gunpowder");
        powder.setItemMeta(meta);

        NBTItem nbtItem = new NBTItem(powder);
        nbtItem.setBoolean(nbtTag.getRef(), true);
        return nbtItem.getItem();
    }
}
