package dev.kyro.pitsim.misc;

import com.xxmicloxx.NoteBlockAPI.model.RepeatMode;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.PositionSongPlayer;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.enums.DeathCry;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;

public class DeathCrys {

    public static void trigger(Player player, DeathCry deathCry, Location location) {

        if(deathCry == DeathCry.MARIO_DEATH) {
            File file = new File("plugins/NoteBlockAPI/Effects/mariodeath.nbs");
            Song song = NBSDecoder.parse(file);
            PositionSongPlayer psp = new PositionSongPlayer(song);
            psp.setTargetLocation(location);
            psp.setDistance(16);
            psp.setRepeatMode(RepeatMode.NO);

            for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {

                psp.addPlayer(onlinePlayer);
            }

            psp.setPlaying(true);

        } else if(deathCry == DeathCry.GHAST_SCREAM) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Sounds.DEATH_GHAST_SCREAM.play(location, 16);
                }
            }.runTaskLater(PitSim.INSTANCE, 2L);
        }

    }
}
