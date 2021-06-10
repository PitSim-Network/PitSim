package dev.kyro.pitsim.controllers;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.events.AttackEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class CombatManager implements Listener {

    int combatTime = 15 * 20;

    public static HashMap<UUID, Integer> taggedPlayers = new HashMap<>();

    static {

        new BukkitRunnable() {
            @Override
            public void run() {

                for(Map.Entry<UUID, Integer> entry : taggedPlayers.entrySet()) {
                    int time = entry.getValue();
                    time = time - 1;

                    if(time > 0) taggedPlayers.put(entry.getKey(), time);
                    else taggedPlayers.remove(entry.getKey());
                }

            }
        }.runTaskTimer(PitSim.INSTANCE, 0L, 1L);



    }


   @EventHandler
   public void onAttack(AttackEvent.Apply attackEvent) {
        Player attacker = attackEvent.attacker;
        Player defender = attackEvent.defender;

        taggedPlayers.put(attacker.getUniqueId(), combatTime);
        taggedPlayers.put(defender.getUniqueId(), combatTime);

   }


   @EventHandler
    public void onLeave(PlayerQuitEvent event) {

        Player player = event.getPlayer();

        if(taggedPlayers.containsKey(player.getUniqueId()) && !player.hasPermission("pitsim.combatlog") && !player.isOp()) {
            player.teleport(Bukkit.getWorld("pit").getSpawnLocation());
            ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
            String command = "ban " + player.getName() + " 1m Logging out while in Combat -s";
            Bukkit.dispatchCommand(console, command);
        }
   }


}
