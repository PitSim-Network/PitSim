package dev.kyro.pitsim.controllers;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class CombatManager {
    public static List<String> taggedPlayers = new ArrayList<>();

    static {


            new BukkitRunnable() {
                @Override
                public void run() {

                    for(String string : taggedPlayers) {

                        String[] tokens = string.split("/");
                        String UUID = tokens[0];
                        int time = Integer.parseInt(tokens[1]);

                        time = time - 1;

                        taggedPlayers.remove(string);

                        if(time > 0) taggedPlayers.add(UUID + "/" + time);
                    }

                }
            }.runTaskTimer(PitSim.INSTANCE, 0L, 20L);



    }

   @EventHandler
   public void onAttack(AttackEvent.Apply attackEvent) {
        Player attacker = attackEvent.attacker;
        Player defender = attackEvent.defender;



   }


}
