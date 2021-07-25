package dev.kyro.pitsim.controllers;

import dev.kyro.arcticapi.misc.ASound;
import dev.kyro.pitsim.PitSim;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.inventivetalent.bossbar.BossBar;
import org.inventivetalent.bossbar.BossBarAPI;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;

public class BossBarManager {

    public static void timerBar(String message, int startminutes, int startseconds, ChatColor numcolor) {


        for(Player player : Bukkit.getOnlinePlayers()) {

            BossBar bossBar = BossBarAPI.addBar(player, new TextComponent(""), BossBarAPI.Color.PINK, BossBarAPI.Style.PROGRESS, 1.0f);

            new BukkitRunnable() {
                int minutes = startminutes;
                int seconds = startseconds;



                @Override
                public void run() {

                    if(seconds > 0) {
                        seconds = seconds - 1;

                    } else {
                        if(minutes > 0) {
                            minutes = minutes - 1;
                            seconds = 59;
                        } else {
                            bossBar.removePlayer(player);
                            this.cancel();
                        }

                    }

//                    int decimal = (minutes * 60) + seconds / (startminutes * 60);
                    String finalseconds = (seconds < 10 ? "0" : "") + seconds;
                    String finalminutes = (minutes < 10 ? "0" : "") + minutes;
                    bossBar.setMessage(message + " " + numcolor + finalminutes + ":" + finalseconds);
//                  bossBar.setProgress((float) ((minutes * 60) + seconds) / (startminutes * 60));
//
//                    Bukkit.broadcastMessage(String.valueOf((minutes * 60) + seconds));
                }
            }.runTaskTimer(PitSim.INSTANCE, 0L, 20L);

        }








    }

}
