package dev.kyro.pitsim.pitevents;

import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.PitEventManager;
import dev.kyro.pitsim.controllers.PlayerManager;
import dev.kyro.pitsim.controllers.objects.PitEvent;
import dev.kyro.pitsim.enums.GameMap;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.*;
import org.bukkit.block.Banner;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftBanner;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestEvent extends PitEvent {
    public static List<Player> red = new ArrayList<>();
    public static List<Player> blue = new ArrayList<>();
    public static Map<Player, ItemStack> helmets = new HashMap<>();
    public static ArmorStand blueArmor;
    public static ArmorStand redArmor;
    public static TestEvent INSTANCE;

    public TestEvent() {
        super("Test Event", 5, true, ChatColor.GREEN);
        INSTANCE = this;
    }

    @Override
    public String getName() {return "Test Event";}

    @Override
    public void prepare() {
        Bukkit.broadcastMessage("Preparing for: " + name);
    }

    @Override
    public void start() {
        teamSetup();
        Bukkit.broadcastMessage("Starting: " + name + ". Ends in " + minutes + " minutes.");

    }

    @Override
    public void end() {
        for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if(helmets.containsKey(onlinePlayer)) onlinePlayer.getInventory().setHelmet(helmets.get(onlinePlayer));
            helmets.remove(onlinePlayer);
        }
        red.clear();
        blue.clear();
        redArmor.remove();
        blueArmor.remove();
        Bukkit.broadcastMessage(name + " has ended.");
    }

    public Location getLocation(String location) {
        if(location == "BlueSpawn") {
            if(MapManager.map == GameMap.DESERT) return new Location(Bukkit.getWorld("pit"), -177, 46, 148);
        }
        if(location == "BlueBanner") {
            if(MapManager.map == GameMap.DESERT) return new Location(Bukkit.getWorld("pit"), -168, 46, 159);
        }
        if(location == "RedSpawn") {
            if(MapManager.map == GameMap.DESERT) return new Location(Bukkit.getWorld("pit"), -75, 45, 269);
        }
        if(location == "RedBanner") {
            if(MapManager.map == GameMap.DESERT) return new Location(Bukkit.getWorld("pit"), -83, 45, 262);
        }
        return null;
    }

    @EventHandler
    public void onEntityAttack(EntityDamageByEntityEvent event) {
        if(event.getEntity() instanceof ArmorStand) event.setCancelled(true);
    }

    public void teamSetup() {

        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());

        if(players.size() > 1) {
            int split = players.size() / 2;

            for(int i = 0; i < split; i++) {
                blue.add(players.get(i));
                players.remove(players.get(i));
            }

            red.addAll(players);
        } else blue.addAll(players);

        ItemStack blueItem = new ItemStack(Material.WOOL, 1, (short) 11);
        ItemMeta blueMeta = blueItem.getItemMeta();
        blueMeta.setDisplayName(ChatColor.BLUE + "Blue Team");
        blueItem.setItemMeta(blueMeta);

        for(Player player : blue) {
            if(!Misc.isAirOrNull(player.getInventory().getHelmet())) {
                helmets.put(player, player.getInventory().getHelmet());
            }
            player.getInventory().setHelmet(blueItem);
            player.teleport(getLocation("BlueSpawn"));
            getLocation("BlueBanner").getBlock().setType(Material.STANDING_BANNER, true);
            Banner blueBanner = (Banner) getLocation("BlueBanner").getBlock().getState();
            blueBanner.setBaseColor(DyeColor.BLUE);
            org.bukkit.material.Banner blueBannerData = (org.bukkit.material.Banner) blueBanner.getData();
            blueBannerData.setFacingDirection(BlockFace.NORTH);
            blueBanner.setData(blueBannerData);
            ArmorStand blueStand = (ArmorStand) Bukkit.getWorld("pit").spawnEntity(getLocation("BlueBanner").add(-0.5, 1.5,0.5), EntityType.ARMOR_STAND);
            blueStand.setVisible(false);
            blueStand.setGravity(false);
            blueStand.setCustomNameVisible(true);
            blueStand.setCustomName(ChatColor.BLUE + "" + ChatColor.BOLD + "Blue Flag");
            blueArmor = blueStand;
        }

        ItemStack redItem = new ItemStack(Material.WOOL, 1, (short) 14);
        ItemMeta redMeta = redItem.getItemMeta();
        redMeta.setDisplayName(ChatColor.RED + "Red Team");
        redItem.setItemMeta(redMeta);

        for(Player player : red) {
            if(!Misc.isAirOrNull(player.getInventory().getHelmet())) {
                helmets.put(player, player.getInventory().getHelmet());
            }
            player.getInventory().setHelmet(redItem);
            player.teleport(getLocation("RedSpawn"));
            getLocation("RedBanner").getBlock().setType(Material.STANDING_BANNER, true);
            Banner redBanner = (Banner) getLocation("RedBanner").getBlock().getState();
            redBanner.setBaseColor(DyeColor.RED);
            org.bukkit.material.Banner redBannerData = (org.bukkit.material.Banner) redBanner.getData();
            redBannerData.setFacingDirection(BlockFace.SOUTH);
            redBanner.setData(redBannerData);
            ArmorStand redStand = (ArmorStand) Bukkit.getWorld("pit").spawnEntity(getLocation("RedBanner").add(-0.5, 1.5,0.5), EntityType.ARMOR_STAND);
            redStand.setVisible(false);
            redStand.setCustomNameVisible(true);
            redStand.setGravity(false);
            redStand.setCustomName(ChatColor.RED + "" + ChatColor.BOLD + "Red Flag");
            redArmor = redStand;
        }
    }
}
