package dev.kyro.pitsim.controllers;

import dev.kyro.pitsim.controllers.objects.Non;
import dev.kyro.pitsim.enums.GameMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class MapManager {

    public static GameMap map = null;

    public static void onStart() {
        double d = Math.random();
        if(d < 0.5) map = GameMap.DESERT;
        else map = GameMap.STARWARS;

        for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.teleport(getPlayerSpawn());
        }
    }

    public static Location playerDesert = new org.bukkit.Location(Bukkit.getWorld("pit"), -108, 86, 194, 48, 3);
    public static Location desertNonSpawn = new Location(Bukkit.getWorld("pit"), -119, 85, 205);
    public static Location desertMid = new Location(Bukkit.getWorld("pit"), -118, 43, 204);
    public static int desertY = 42;

    public static Location playerSnow = new org.bukkit.Location(Bukkit.getWorld("pit"), -99, 46, 707, 0, 0);
    public static Location snowNonSpawn = new Location(Bukkit.getWorld("pit"), -99, 46, 716, -90, 0);
    public static Location snowMid = new Location(Bukkit.getWorld("pit"), -98, 6, 716);
    public static int snowY = 4;

    public static Location getNonSpawn() {

        Location spawn = map == GameMap.STARWARS ? snowNonSpawn : desertNonSpawn;
        spawn = spawn.clone();
        spawn.setX(spawn.getX() + (Math.random() * 6 - 3));
        spawn.setZ(spawn.getZ() + (Math.random() * 6 - 3));
        return spawn;
    }

    public static Location getMid() {

        if(map == GameMap.STARWARS) return snowMid;
        else return desertMid;
    }

    public static int getY() {
        if(map == GameMap.STARWARS) return snowY;
        else return desertY;
    }

    public static Location getPlayerSpawn() {
        if(map == GameMap.STARWARS) return playerSnow;
        else return playerDesert;
    }

    public static void onSwitch() {
        for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.teleport(getPlayerSpawn());
        }

        for(Non non : NonManager.nons) {
            non.respawn();
        }
    }
}
