package dev.kyro.pitsim.brewing.ingredients;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.pitsim.brewing.PotionManager;
import dev.kyro.pitsim.brewing.objects.BrewingIngredient;
import dev.kyro.pitsim.brewing.objects.PotionEffect;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class EnderPearl extends BrewingIngredient {
    public static EnderPearl INSTANCE;
    public EnderPearl() {
        super(10, NBTTag.ENDERMAN_PEARL, "Venom", ChatColor.GREEN, PotionType.POISON);
        INSTANCE = this;
    }

    @Override
    public void administerEffect(Player player, BrewingIngredient potency, int duration) {
        Misc.applyPotionEffect(player, PotionEffectType.POISON, duration, 0, false, false);
    }

    @EventHandler
    public void onHit(AttackEvent.Pre attackEvent) {
        PotionEffect attackerEffect = PotionManager.getEffect(attackEvent.attackerPlayer, this);
        if(attackerEffect == null) return;

        int tokensToRemove = (int) getPotency(attackerEffect.potency);

        for (Map.Entry<PitEnchant, Integer> entry : attackEvent.getAttackerEnchantMap().entrySet()) {
            if(entry.getKey().applyType != ApplyType.SWORDS && entry.getKey().applyType != ApplyType.BOWS && entry.getKey().applyType != ApplyType.MELEE) continue;
            for (int i = 0; i < entry.getValue(); i++) {
                attackEvent.getAttackerEnchantMap().put(entry.getKey(), entry.getValue() - 1);

                tokensToRemove--;
                if(tokensToRemove == 0) return;
            }
        }
    }

    @EventHandler
    public void onDefend(AttackEvent.Apply defendEvent) {
        PotionEffect defenderEffect = PotionManager.getEffect(defendEvent.defenderPlayer, this);
        if(defenderEffect == null) return;

        int tokensToRemove = (int) getPotency(defenderEffect.potency) / 2;

        for (Map.Entry<PitEnchant, Integer> entry : defendEvent.getDefenderEnchantMap().entrySet()) {
            if(entry.getKey().applyType != ApplyType.PANTS) continue;
            for (int i = 0; i < entry.getValue(); i++) {
                defendEvent.getDefenderEnchantMap().put(entry.getKey(), entry.getValue() - 1);

                tokensToRemove--;
                if(tokensToRemove == 0) return;
            }

        }
    }



    @Override
    public Object getPotency(BrewingIngredient potencyIngredient) {
        return potencyIngredient.tier;
    }

    @Override
    public List<String> getPotencyLore(BrewingIngredient potency) {
        List<String> lore = new ArrayList<>();

        lore.add("");
        lore.add(ChatColor.GRAY + "Disables " + color + getPotency(potency) + " Mystic Tokens " +  ChatColor.GRAY + "from being");
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
