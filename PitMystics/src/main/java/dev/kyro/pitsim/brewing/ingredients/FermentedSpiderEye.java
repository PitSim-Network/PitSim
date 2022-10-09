package dev.kyro.pitsim.brewing.ingredients;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.pitsim.brewing.PotionManager;
import dev.kyro.pitsim.brewing.objects.BrewingIngredient;
import dev.kyro.pitsim.brewing.objects.PotionEffect;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FermentedSpiderEye extends BrewingIngredient {
    public static FermentedSpiderEye INSTANCE;
    public FermentedSpiderEye() {
        super(5, NBTTag.CAVESPIDER_EYE, "Neutrality", ChatColor.YELLOW, PotionType.REGEN);
        INSTANCE = this;
    }

    @Override
    public void administerEffect(Player player, BrewingIngredient potency, int duration) {

    }

    @EventHandler
    public void onHit(AttackEvent.Pre event) {
        PotionEffect defenderEffect = PotionManager.getEffect(event.getDefenderPlayer(), this);

        if(defenderEffect != null) {
            double chance = (double) getPotency(defenderEffect.potency);
            boolean isProtected = Math.random() <= chance;

            if(isProtected) {
                event.setCancelled(true);
                event.getEvent().setCancelled(true);
//                Sounds.AEGIS.play(event.defenderPlayer.getLocation());
                event.getDefenderPlayer().getWorld().playEffect(event.defenderPlayer.getLocation(), Effect.EXPLOSION_LARGE, Effect.EXPLOSION_LARGE.getData(), 100);
            }
        }

        PotionEffect attackerEffect = PotionManager.getEffect(event.getAttackerPlayer(), this);

        if(attackerEffect != null) {
            double attackerChance = (double) getPotency(attackerEffect.potency);
            boolean attackerIsProtected = Math.random() <= attackerChance;

            if(attackerIsProtected) {
                event.setCancelled(true);
                event.getEvent().setCancelled(true);
                Sounds.AEGIS.play(event.getDefenderPlayer().getLocation());
                event.getDefenderPlayer().getWorld().playEffect(event.getDefenderPlayer().getLocation(), Effect.EXPLOSION_LARGE, Effect.EXPLOSION_LARGE.getData(), 100);
            }
        }
    }

    @Override
    public Object getPotency(BrewingIngredient potencyIngredient) {
        return 0.1 * potencyIngredient.tier;
    }

    @Override
    public List<String> getPotencyLore(BrewingIngredient potency) {
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.WHITE + "" + (int) ((double) getPotency(potency) * 100) + "% " + ChatColor.GRAY + "chance to " + color + "deflect " + ChatColor.GRAY + "incoming");
        lore.add(ChatColor.GRAY + "hits and " + color + "cancel " + ChatColor.GRAY + "out going hits.");
        return lore;
    }

    @Override
    public int getDuration(BrewingIngredient durationIngredient) {
        return 2 * 60 * 20 * durationIngredient.tier;
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
