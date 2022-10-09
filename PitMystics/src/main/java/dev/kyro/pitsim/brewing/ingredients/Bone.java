package dev.kyro.pitsim.brewing.ingredients;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.pitsim.brewing.PotionManager;
import dev.kyro.pitsim.brewing.objects.BrewingIngredient;
import dev.kyro.pitsim.brewing.objects.PotionEffect;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.events.KillEvent;
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

public
class Bone extends BrewingIngredient {
    public static Bone INSTANCE;

    public Bone() {
        super(2, NBTTag.SKELETON_BONE,"XP Boost", ChatColor.AQUA, PotionType.SPEED);
        INSTANCE = this;
    }

    @Override
    public void administerEffect(Player player, BrewingIngredient potency, int duration) {

    }

    @EventHandler
    public void onKill(KillEvent event) {
        PotionEffect effect = PotionManager.getEffect(event.getKiller(), this);
        if(effect == null) return;

        String[] values = ((String) getPotency(effect.potency)).split(",");
        event.bonusXpReward += Integer.parseInt(values[0]);
        event.xpCap += Integer.parseInt(values[1]);
    }

    @Override
    public Object getPotency(BrewingIngredient potencyIngredient) {
        int tier = potencyIngredient.tier;

        return 10 * tier + "," + 20 * tier;
    }

    @Override
    public List<String> getPotencyLore(BrewingIngredient potency) {
        List<String> lore = new ArrayList<>();
        String[] values = ((String)getPotency(potency)).split(",");

        lore.add("");
        lore.add(ChatColor.GRAY + "Earn " + ChatColor.AQUA + "+" + values[0] + " XP " + ChatColor.GRAY + "and " + ChatColor.AQUA + "+" + values[1] + " Max XP");
        lore.add(ChatColor.GRAY + "from kills and assists.");
        return lore;
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
