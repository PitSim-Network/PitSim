package dev.kyro.pitsim.enchants.tainted;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enchants.GottaGoFast;
import dev.kyro.pitsim.enums.ApplyType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public
class Sonic extends PitEnchant {
    public static Map<Player, Integer> speedMap = new HashMap<>();
    public static Sonic INSTANCE;

    public Sonic() {
        super("Sonic", true, ApplyType.CHESTPLATES, "sonic", "sanic", "fast");
        tainted = true;
        INSTANCE = this;
    }

    static {
        new BukkitRunnable() {
            @Override
            public void run() {
                for(Player player : Bukkit.getOnlinePlayers()) {
                    Map<PitEnchant, Integer> enchantMap = EnchantManager.getEnchantsOnPlayer(player);
                    int enchantLvl = enchantMap.getOrDefault(INSTANCE, 0);
                    int oldEnchantLvl = speedMap.getOrDefault(player, 0);
                    int gtgfTier = EnchantManager.getEnchantLevel(player, GottaGoFast.INSTANCE);

                    if(!MapManager.inDarkzone(player)) enchantLvl = 0;

                    if(enchantLvl == oldEnchantLvl && gtgfTier > 0) continue;

                    if(enchantLvl != oldEnchantLvl) {
                        speedMap.put(player, enchantLvl);

                        int finalEnchantLvl = enchantLvl;
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                player.setWalkSpeed(getWalkSpeed(finalEnchantLvl));
                            }
                        }.runTask(PitSim.INSTANCE);
                    }
                }
            }
        }.runTaskTimerAsynchronously(PitSim.INSTANCE, 0, 20);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.getPlayer().setWalkSpeed(0.2F);
    }

    @Override
    public List<String> getDescription(int enchantLvl) {
        return new ALoreBuilder("&7Move &e100% &7faster at all times", "&d&o-" + reduction(enchantLvl) + "% Mana Regen").getLore();
    }

    public static float getWalkSpeed(int enchantLvl) {
        if(enchantLvl == 0) return 0.2F;
        else return 0.5F;
    }

    public static int reduction(int enchantLvl) {
        return 80 - (20 * enchantLvl);
    }
}
