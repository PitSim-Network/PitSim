package dev.kyro.pitsim.controllers;

import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.ASound;
import dev.kyro.pitsim.controllers.objects.Non;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.misc.Misc;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class LevelManager {

    public static List<Long> levelMap = new ArrayList<>();

    static {

        for(int i = 0; i < 2000; i++) {
            levelMap.add(getXP(i));
        }

        for(int i = 0; i < levelMap.size(); i++) {
//            System.out.println(i + " " + levelMap.get(i));
        }
    }

    public static int getLevel(long xp) {

        for(int i = 0; i < levelMap.size(); i++) {

            long lvlXP = levelMap.get(i);
            if(xp < lvlXP) continue;
            return i;
        }

        return -1;
    }

    public static long getXP(long level) {

        return (long) (9 + 10 * level + Math.pow(level, 2.3) + Math.pow(1.015, level)) * 100;
    }

    public static long getXPToNextLvl(long currentXP) {

        int currentLvl = getLevel(currentXP);
        return getXP(currentLvl + 1) - getXP(currentLvl);
    }

    public static int getPlayerKills(long level) {
        return (int) ((9 + 10 * level + Math.pow(level, 2.3) + Math.pow(1.015, level)) / 20);
    }

    public static void incrementLevel(Player player) {
        PitPlayer pitplayer = PitPlayer.getPitPlayer(player);
        setXPBar(player, pitplayer);
        if(NonManager.getNon(player) == null && pitplayer.remainingXP == 0 && pitplayer.playerKills >= getPlayerKills(pitplayer.playerLevel)) {
            pitplayer.remainingXP  = (int) getXP(pitplayer.playerLevel + 1);
            pitplayer.playerLevel = pitplayer.playerLevel + 1;
            pitplayer.playerKills = 0;
            setXPBar(player, pitplayer);

            ASound.play(player, Sound.LEVEL_UP, 1, 1);
            String message = ChatColor.translateAlternateColorCodes('&', "&e&lLEVEL UP! %luckperms_prefix%%player_name% &7has reached level &e" + pitplayer.playerLevel);
            Bukkit.broadcastMessage(PlaceholderAPI.setPlaceholders(player, message));

            Misc.sendTitle(player, "&e&lLEVEL UP!", 40);
            Misc.sendSubTitle(player, "&e" + (pitplayer.playerLevel - 1) + " &7\u279F &e" + pitplayer.playerLevel, 40);

            if(NonManager.getNon(player) != null) return;
            FileConfiguration playerData = APlayerData.getPlayerData(player);
            playerData.set("level", pitplayer.playerLevel);
            playerData.set("playerkills", pitplayer.playerKills);
            playerData.set("xp", pitplayer.remainingXP);
            APlayerData.savePlayerData(player);
        }
    }

    public static void setXPBar(Player player, PitPlayer pitPlayer) {
        if(NonManager.getNon(player) != null) return;

        player.setLevel(pitPlayer.playerLevel);
        float remaining = pitPlayer.remainingXP;
        float total = (int) getXP(pitPlayer.playerLevel);

        player.setLevel(pitPlayer.playerLevel);
        float xp = (total - remaining) /  total;

        player.setExp(xp);

    }
}