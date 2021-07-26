package dev.kyro.pitsim.controllers;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class MapManager {

    public static Location playerDesert = new org.bukkit.Location(Bukkit.getWorld("pit"), -108, 86, 194, 48, 3);
    public static Location desertNonSpawn = new Location(Bukkit.getWorld("pit"), -119, 43, 205);
    public static Location desertMid = new Location(Bukkit.getWorld("pit"), -118, 43, 204);
    public static int desertY = 42;

    public static Location playerSnow = new org.bukkit.Location(Bukkit.getWorld("pit"), -99, 46, 707, 0, 0);
    public static Location snowNonSpawn = new Location(Bukkit.getWorld("pit"), -99, 55, 716, -90, 0);
    public static Location snowMid = new Location(Bukkit.getWorld("pit"), -98, 5, 716);
    public static int snowY = 4;

    public static Location getNonSpawn() {

        return snowNonSpawn;
    }

    public static Location getMid() {

        return snowMid;
    }

    public static int getY() {
        return snowY;
    }

    public static Location getPlayerSpawn() {
        return playerSnow;
    }
}
