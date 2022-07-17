package dev.kyro.pitsim.brewing.objects;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.brewing.BrewingManager;
import dev.kyro.pitsim.brewing.ingredients.SpiderEye;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class BrewingSession {

    public Player player;
    public int brewingSlot;
    public String saveString;
    public BrewingIngredient identifier;
    public BrewingIngredient potency;
    public BrewingIngredient duration;
    public BrewingIngredient reduction;
    public long startTime;

    public BrewingSession(Player player, int brewingSlot, String saveString, BrewingIngredient identifier, BrewingIngredient potency, BrewingIngredient duration, BrewingIngredient reduction) {
        this.player = player;
        this.brewingSlot = brewingSlot;
        this.saveString = saveString;

        if(saveString != null) loadFromSave();
        else {
            this.identifier = identifier;
            this.potency = potency;
            this.duration = duration;
            this.reduction = reduction;
            startTime = System.currentTimeMillis();
            PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
            pitPlayer.brewingSessions[brewingSlot - 1] = getSaveString();
            pitPlayer.save();
        }
    }

    public void loadFromSave() {
        String[] saveValues = saveString.split(",");
        brewingSlot = Integer.parseInt(saveValues[0]);
        identifier = BrewingIngredient.getIngredientFromTier(Integer.parseInt(saveValues[1]));
        potency = BrewingIngredient.getIngredientFromTier(Integer.parseInt(saveValues[2]));
        duration = BrewingIngredient.getIngredientFromTier(Integer.parseInt(saveValues[3]));
        reduction = BrewingIngredient.getIngredientFromTier(Integer.parseInt(saveValues[4]));
        startTime = Long.parseLong(saveValues[5]);
    }

    public void givePotion() {
        Potion rawPotion = new Potion(identifier.potionType);
        ItemStack potion = rawPotion.toItemStack(1);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();
        meta.addCustomEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 1, 0, false, false), true);
        meta.setDisplayName(identifier.color + "Tier " + AUtil.toRoman(potency.tier) + " " + identifier.name + " Potion");
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        List<String> lore = new ArrayList<>(identifier.getPotencyLore(potency));
        lore.add("");
        if(identifier instanceof SpiderEye)  lore.add(ChatColor.GRAY + "Duration: " + ChatColor.WHITE + "INSTANT!");
        else lore.add(ChatColor.GRAY + "Duration: " + ChatColor.WHITE + Misc.ticksToTimeUnformatted(identifier.getDuration(duration)));
        lore.add("");
        lore.add(identifier.color + "Tainted Potion");
        meta.setLore(lore);
        potion.setItemMeta(meta);
        NBTItem nbtItem = new NBTItem(potion);
        nbtItem.setInteger(NBTTag.POTION_IDENTIFIER.getRef(), identifier.tier);
        nbtItem.setInteger(NBTTag.POTION_POTENCY.getRef(), potency.tier);
        nbtItem.setInteger(NBTTag.POTION_DURATION.getRef(), duration.tier);
        AUtil.giveItemSafely(player, nbtItem.getItem());
        BrewingManager.brewingSessions.remove(this);
        PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
        pitPlayer.brewingSessions[brewingSlot - 1] = null;
        pitPlayer.save();
    }


    public String getSaveString() {
        return brewingSlot + "," + identifier.tier + "," + potency.tier + "," +
                duration.tier + "," + reduction.tier + "," + startTime;
    }


}
