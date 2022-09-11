package dev.kyro.pitsim.controllers;

import be.maximvdw.featherboard.FeatherBoard;
import be.maximvdw.featherboard.api.FeatherBoardAPI;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enchants.Moctezuma;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class ScoreboardManager {

    public static void init() {};

    public static List<Player> goldScoreboardPlayers = new ArrayList<>();

    public static List<PitEnchant> goldEnchants = Arrays.asList(EnchantManager.getEnchant("moct"),
            EnchantManager.getEnchant("gboost"), EnchantManager.getEnchant("gbump"));

    static {
        new BukkitRunnable() {
            @Override
            public void run() {
                for(Player player : Bukkit.getOnlinePlayers()) {
                    ItemStack leggings = player.getInventory().getLeggings();
                    if(Misc.isAirOrNull(leggings)) continue;

                    int count = 0;
                    for(PitEnchant goldEnchant : goldEnchants) {
                        if(EnchantManager.getEnchantsOnItem(leggings).containsKey(goldEnchant)) count++;
                    }

                    if(count >= 2 && !goldScoreboardPlayers.contains(player) && player.getWorld() != MapManager.getDarkzone()) {
                        goldScoreboardPlayers.add(player);
                        FeatherBoardAPI.showScoreboard(player, "gold");
                    } else if(count < 2) {
                        goldScoreboardPlayers.remove(player);
                        if(player.getWorld() != MapManager.getDarkzone()) FeatherBoardAPI.showScoreboard(player, "default");
                    }

                }
            }
        }.runTaskTimerAsynchronously(PitSim.INSTANCE, 20 * 5, 20 * 5);
    }
}
