package dev.kyro.pitsim.controllers;

import com.xxmicloxx.NoteBlockAPI.model.RepeatMode;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.PositionSongPlayer;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import dev.kyro.arcticapi.misc.ASound;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.Non;
import dev.kyro.pitsim.controllers.objects.PitEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.pitevents.TestEvent;
import dev.kyro.pitsim.pitevents.TestEvent2;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.inventivetalent.bossbar.BossBar;
import org.inventivetalent.bossbar.BossBarAPI;

import java.awt.font.GraphicAttribute;
import java.io.File;
import java.util.*;

public class PitEventManager {
    public static List<PitEvent> events = new ArrayList<>();

    public static Boolean majorEvent = false;

    public static void eventWait() {
    events.add(new TestEvent());
    events.add(new TestEvent2());


        new BukkitRunnable() {
            @Override
            public void run() {

                PitEvent randomEvent = getRandomEvent(events);
                startTimer(randomEvent);

                File file = new File("plugins/NoteBlockAPI/Effects/major.nbs");
                Song song = NBSDecoder.parse(file);
                RadioSongPlayer rsp = new RadioSongPlayer(song);
                rsp.setRepeatMode(RepeatMode.NO);
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&5&lMAJOR EVENT! " +
                        " " + randomEvent.color + ""  + ChatColor.BOLD + randomEvent.name.toUpperCase(Locale.ROOT) + " &7in 30 seconds [&e&lINFO&7]"));

                for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {

                    rsp.addPlayer(onlinePlayer);
                }
                rsp.setPlaying(true);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        BossBarManager.timerBar(ChatColor.translateAlternateColorCodes('&',
                                "&5&lMAJOR EVENT! " + randomEvent.color + "" + ChatColor.BOLD +
                                        randomEvent.getName().toUpperCase(Locale.ROOT)) + "! &7Starting in", 0, 30, ChatColor.YELLOW);

                    }
                }.runTaskLater(PitSim.INSTANCE, 10L);



                randomEvent.prepare();


            }
        }.runTaskTimer(PitSim.INSTANCE, 0L, 2000L);

    }

    public static void startTimer(PitEvent event) {

        new BukkitRunnable() {
            @Override
            public void run() {

                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&5&lMAJOR EVENT! " +
                        " " + event.color + ""  + ChatColor.BOLD + event.name.toUpperCase(Locale.ROOT) + " &7starting now"));
                endTimer(event);
                event.start();
                majorEvent = true;
                for(Player player : Bukkit.getOnlinePlayers()) {
                    Misc.sendTitle(player,event.color + "" + ChatColor.BOLD + "PIT EVENT!", 50);
                    Misc.sendSubTitle(player, event.color + "" + ChatColor.BOLD + event.name.toUpperCase(Locale.ROOT), 50);
                    ASound.play(player, Sound.ENDERDRAGON_GROWL, 2  , 1);

                }

                for(Non non : NonManager.nons) {
                    non.setDisabled(true);
                }

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        BossBarManager.timerBar(ChatColor.translateAlternateColorCodes('&',
                                "&5&lMAJOR EVENT! " + event.color + "" + ChatColor.BOLD +
                                        event.getName().toUpperCase(Locale.ROOT)) + "! &7Ending in", 1, 0, ChatColor.GREEN);
                    }
                }.runTaskLater(PitSim.INSTANCE, 10L);


            }
        }.runTaskLater(PitSim.INSTANCE, 600L);
    }

    public static void endTimer(PitEvent event) {

        new BukkitRunnable() {
            @Override
            public void run() {

                Bukkit.broadcastMessage("MAJOR EVENT: " + event.getName() + " ended");
                event.end();
                majorEvent = false;
                for(Non non : NonManager.nons) {
                    non.setDisabled(false);
                }


            }
        }.runTaskLater(PitSim.INSTANCE, 1200L);
    }

    public static PitEvent getRandomEvent(List<PitEvent> events) {
        Random rand = new Random();
        return events.get(rand.nextInt(events.size()));
    }

}
