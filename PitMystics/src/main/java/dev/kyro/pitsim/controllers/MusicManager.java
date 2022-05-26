package dev.kyro.pitsim.controllers;

import com.xxmicloxx.NoteBlockAPI.NoteBlockAPI;
import com.xxmicloxx.NoteBlockAPI.model.RepeatMode;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.EntitySongPlayer;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import dev.kyro.pitsim.PitSim;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MusicManager implements Listener {

    public static List<EntitySongPlayer> songs = new ArrayList<>();

    static {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if(!MapManager.inDarkzone(player) || NoteBlockAPI.isReceivingSong(player)) continue;
                    File file = new File("plugins/NoteBlockAPI/Effects/darkzone.nbs");
                    Song song = NBSDecoder.parse(file);
                    EntitySongPlayer esp = new EntitySongPlayer(song);
                    esp.setEntity(player);
                    esp.setDistance(16);
                    esp.setRepeatMode(RepeatMode.ONE);
                    esp.addPlayer(player);
                    esp.setPlaying(true);
                    songs.add(esp);
                }
            }
        }.runTaskTimer(PitSim.INSTANCE, 20, 20);
    }

    public static boolean playerIsListening(Player player) {
        for (EntitySongPlayer song : songs) {
            if(song.getEntity() == player) return true;
        }
        return false;
    }


    public static void stopPlaying(Player player) {
        NoteBlockAPI.stopPlaying(player);
        EntitySongPlayer toRemove = null;
        for (EntitySongPlayer song : songs) {
            if(song.getEntity() == player) {
                toRemove = song;
            }
        }
        if(toRemove != null) songs.remove(toRemove);
    }
}
