package dev.kyro.pitsim.brewing;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.brewing.objects.BrewingIngredient;
import dev.kyro.pitsim.brewing.objects.PotionEffect;
import dev.kyro.pitsim.enums.NBTTag;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class PotionManager implements Listener {

    public static List<PotionEffect> potionEffectList = new ArrayList<>();
    public static Map<Player, Integer> playerIndex = new HashMap<>();

    static {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (PotionEffect potionEffect : potionEffectList) {
                    potionEffect.tick();
                }
            }
        }.runTaskTimer(PitSim.INSTANCE, 1, 1);


        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    List<PotionEffect> effects = getPotionEffects(player);
                    if(effects.size() == 0) continue;

                    playerIndex.putIfAbsent(player, 0);
                    int index = playerIndex.get(player);

                    while(index >= effects.size()) {
                        index--;
                    }

                    StringBuilder builder = new StringBuilder();
                    for (PotionEffect effect : effects) {
                        if(effect == effects.get(index)) builder.append(effect.potionType.color + "" + ChatColor.BOLD + effect.potionType.name.toUpperCase(Locale.ROOT)).append(" ").append(AUtil.toRoman(effect.potency.tier));
                        else builder.append(effect.potionType.color + effect.potionType.name).append(" ").append(AUtil.toRoman(effect.potency.tier));
                        if(effect != effects.get(effects.size() - 1)) builder.append(ChatColor.GRAY + ", ");
                    }

                    int maxI = effects.size() - 1;
                    if(playerIndex.get(player) + 1 > maxI) {
                        playerIndex.put(player, 0);
                    } else playerIndex.put(player, playerIndex.get(player) + 1);

                    Bukkit.broadcastMessage(builder.toString());
                }
            }
        }.runTaskTimer(PitSim.INSTANCE, 20 * 3, 20 * 3);
    }
    
    @EventHandler
    public void onPotionDrink(PlayerItemConsumeEvent event) {
       if(event.getItem().getType() != Material.POTION) return;
       Player player = event.getPlayer();

       event.setCancelled(true);

       ItemStack potion = event.getItem();
        NBTItem nbtItem = new NBTItem(potion);
        if(!nbtItem.hasKey(NBTTag.POTION_IDENTIFIER.getRef())) return;

        BrewingIngredient identifier = BrewingIngredient.getIngredientFromTier(nbtItem.getInteger(NBTTag.POTION_IDENTIFIER.getRef()));
        BrewingIngredient potency = BrewingIngredient.getIngredientFromTier(nbtItem.getInteger(NBTTag.POTION_POTENCY.getRef()));
        BrewingIngredient duration = BrewingIngredient.getIngredientFromTier(nbtItem.getInteger(NBTTag.POTION_DURATION.getRef()));

        if(hasLesserEffect(player, identifier, potency)) {
            AOutput.send(player, "&5&lPOTION! &7You already have a stonger tier of this effect!");
            return;
        }
        replaceLesserEffects(player, identifier, potency);
        player.setItemInHand(new ItemStack(Material.AIR));

        assert identifier != null;
        assert potency != null;
        potionEffectList.add(new PotionEffect(player, identifier, potency, duration));
    }

    public boolean hasLesserEffect(Player player, BrewingIngredient identifier, BrewingIngredient potency) {
        for (PotionEffect potionEffect : potionEffectList) {
            if(potionEffect.player != player) continue;
            if(potionEffect.potionType != identifier) continue;

            if(potionEffect.potency.tier > potency.tier) {
                return true;
            }
        }
        return false;
    }

    public void replaceLesserEffects(Player player, BrewingIngredient identifier, BrewingIngredient potency) {
        for (PotionEffect potionEffect : potionEffectList) {
            if(potionEffect.player != player) continue;
            if(potionEffect.potionType != identifier) continue;

            if(potionEffect.potency.tier <= potency.tier) {
                potionEffect.onExpire();
                return;
            }
        }
    }


    public static List<PotionEffect> getPotionEffects(Player player) {
        List<PotionEffect> effects = new ArrayList<>();
        for (PotionEffect potionEffect : potionEffectList) {
            if(potionEffect.player == player) effects.add(potionEffect);
        }
        return effects;
    }
}
