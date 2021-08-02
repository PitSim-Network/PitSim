package dev.kyro.pitsim.pitevents;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.LeaderboardManager;
import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.PitEventManager;
import dev.kyro.pitsim.controllers.objects.PitEvent;
import dev.kyro.pitsim.enums.GameMap;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.events.OofEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.*;
import org.bukkit.block.Banner;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class TestEvent extends PitEvent {
    public static List<Player> red = new ArrayList<>();
    public static List<Player> blue = new ArrayList<>();
    public static Map<Player, ItemStack> helmets = new HashMap<>();
    public static Map<Player, Integer> captures = new HashMap<>();
    public static ArmorStand blueArmor;
    public static ArmorStand redArmor;
    public static Player blueBannerHolder;
    public static Player redBannerHolder;
    public static ItemStack blueHat;
    public static ItemStack redHat;
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
        PitEventManager.activeEvent = this;
        Bukkit.broadcastMessage("Starting: " + name + ". Ends in " + minutes + " minutes.");

    }

    @Override
    public void end() {
        for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if(helmets.containsKey(onlinePlayer)) onlinePlayer.getInventory().setHelmet(helmets.get(onlinePlayer));
            else onlinePlayer.getInventory().setHelmet(new ItemStack(Material.AIR));
            helmets.remove(onlinePlayer);
        }
        red.clear();
        blue.clear();
        redArmor.remove();
        blueArmor.remove();
        redBannerHolder = null;
        blueBannerHolder = null;
        getLocation("BlueBanner").getBlock().setType(Material.AIR, true);
        getLocation("RedBanner").getBlock().setType(Material.AIR, true);
        getTopThree();
        captures.clear();
        PitEventManager.activeEvent = null;

        Bukkit.broadcastMessage(name + " has ended.");
    }

    public void getTopThree() {
        Map<String, Integer> stringPlayers = new HashMap<>();
        String playerOne = null;
        String playerTwo = null;
        String playerThree = null;
        String messageOne = null;
        String messageTwo = null;
        String messageThree = null;

        for(Map.Entry<Player, Integer> pair : captures.entrySet()) {
            stringPlayers.put(pair.getKey().getName(), pair.getValue());

        }
        Map<String, Integer> sortedMap = LeaderboardManager.calculateEvent(stringPlayers);
            if(sortedMap.size() >= 1) {
                 playerOne = (String) sortedMap.keySet().toArray()[0];
            }
            if(sortedMap.size() >= 2) {
                 playerTwo = (String) sortedMap.keySet().toArray()[1];
            }
            if(sortedMap.size() >= 3) {
                 playerThree = (String) sortedMap.keySet().toArray()[2];
            }

        for(Player player : Bukkit.getOnlinePlayers()) {
            Bukkit.broadcastMessage(captures.toString());
            if(player.getDisplayName().equalsIgnoreCase(playerOne)) {
                messageOne = player.getDisplayName() + " with " + captures.get(player) + " captures!";
            }
            if(player.getDisplayName().equalsIgnoreCase(playerTwo)) {
                messageTwo = player.getDisplayName() + " with " + captures.get(player) + " captures!";
            }
            if(player.getDisplayName().equalsIgnoreCase(playerThree)) {
                messageThree = player.getDisplayName() + " with " + captures.get(player) + " captures!";
            }
        }

        if(messageOne != null) Bukkit.broadcastMessage(messageOne);
        if(messageTwo != null) Bukkit.broadcastMessage(messageTwo);
        if(messageThree != null) Bukkit.broadcastMessage(messageThree);
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

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if(!PitEventManager.majorEvent) return;
        if(blueArmor !=  null) {
            List<Entity> blueBannerPlayers = blueArmor.getNearbyEntities(2, 2, 2);
            for(Entity blueBannerPlayer : blueBannerPlayers) {
                if(blueBannerPlayer instanceof Player && !blue.contains(blueBannerPlayer) && blueBannerHolder == null) {
                    Bukkit.broadcastMessage("Blue Banner taken by " + ((Player) blueBannerPlayer).getDisplayName() + "!");
                    blueBannerHolder = (Player) blueBannerPlayer;
                    blueBannerHolder.getInventory().setHelmet(new ItemStack(Material.BANNER, 1, (short) 4));
                    getLocation("BlueBanner").getBlock().setType(Material.AIR);
                    blueArmor.setCustomName(ChatColor.BLUE + "Blue Banner Taken by " + ((Player) blueBannerPlayer).getDisplayName());
                    for(Player player : blue) {
                        Misc.sendTitle(player, ChatColor.BLUE + "Banner Stolen!", 40);
                    }
                }
                if(blueBannerPlayer instanceof Player && blue.contains(blueBannerPlayer) && redBannerHolder == blueBannerPlayer) {
                    ((Player) blueBannerPlayer).getInventory().setHelmet(blueHat);
                    captures.put((Player) blueBannerPlayer, captures.get(blueBannerPlayer) + 1);
                    Bukkit.broadcastMessage(String.valueOf(captures.get(blueBannerPlayer)));
                    redBannerHolder  = null;
                    setupRedBanner();
                    Bukkit.broadcastMessage(((Player) blueBannerPlayer).getDisplayName() + " has captured the Red Banner!");
                }
            }
        }
        if(redArmor !=  null) {
            List<Entity> redBannerPlayers = redArmor.getNearbyEntities(2, 2, 2);
            for(Entity redBannerPlayer : redBannerPlayers) {
                if(redBannerPlayer instanceof Player && !red.contains(redBannerPlayer) && redBannerHolder == null) {
                    Bukkit.broadcastMessage("Red Banner taken by " + ((Player) redBannerPlayer).getDisplayName() + "!");
                    redBannerHolder = (Player) redBannerPlayer;
                    redBannerHolder.getInventory().setHelmet(new ItemStack(Material.BANNER, 1, (short) 1));
                    getLocation("RedBanner").getBlock().setType(Material.AIR);
                    redArmor.setCustomName(ChatColor.RED + "Red Banner Taken by " + ((Player) redBannerPlayer).getDisplayName());
                    for(Player player : red) {
                        Misc.sendTitle(player, ChatColor.RED + "Banner Stolen!", 40);
                    }
                }
                if(redBannerPlayer instanceof Player && red.contains(redBannerPlayer) && blueBannerHolder == redBannerPlayer) {
                    ((Player) redBannerPlayer).getInventory().setHelmet(redHat);
                    captures.put((Player) redBannerPlayer, captures.get(redBannerPlayer) + 1);
                    Bukkit.broadcastMessage(String.valueOf(captures.get(redBannerPlayer)));
                    blueBannerHolder = null;
                    setupBlueBanner();
                    Bukkit.broadcastMessage(((Player) redBannerPlayer).getDisplayName() + " has captured the Blue Banner!");
                }
            }
        }
    }

    @EventHandler
    public void onKill(KillEvent killEvent) {
        if(red.contains(killEvent.dead)) {
            if(blueBannerHolder == killEvent.dead) {
                blueBannerHolder = null;
                killEvent.dead.getInventory().setHelmet(redHat);
                Bukkit.broadcastMessage(killEvent.dead.getDisplayName() + "  was killed. The Blue Banner was returned to its base.");
                setupBlueBanner();
                for(Player player : blue) {
                    Misc.sendTitle(player, ChatColor.BLUE + "Banner Returned!", 40);
                }
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    killEvent.dead.teleport(getLocation("RedSpawn"));
                }
            }.runTaskLater(PitSim.INSTANCE, 1L);
        }
        if(blue.contains(killEvent.dead)) {
            if(redBannerHolder == killEvent.dead) {
                redBannerHolder = null;
               killEvent.dead.getInventory().setHelmet(blueHat);
                Bukkit.broadcastMessage(killEvent.dead.getDisplayName() + "  was killed. The Red Banner was returned to its base.");
                setupRedBanner();
                for(Player player : red) {
                    Misc.sendTitle(player, ChatColor.RED + "Banner Returned!", 40);
                }
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    killEvent.dead.teleport(getLocation("BlueSpawn"));
                }
            }.runTaskLater(PitSim.INSTANCE, 1L);
        }
    }

    @EventHandler
    public void onOof(OofEvent oofEvent) {
        if(red.contains(oofEvent.getPlayer())) {
            if(blueBannerHolder == oofEvent.getPlayer()) {
                blueBannerHolder = null;
                oofEvent.getPlayer().getInventory().setHelmet(redHat);
                Bukkit.broadcastMessage(oofEvent.getPlayer().getDisplayName() + "  was killed. The Blue Banner was returned to its base.");
                setupBlueBanner();
                for(Player player : blue) {
                    Misc.sendTitle(player, ChatColor.BLUE + "Banner Returned!", 40);
                }
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                   oofEvent.getPlayer().teleport(getLocation("RedSpawn"));
                }
            }.runTaskLater(PitSim.INSTANCE, 1L);
        }
        if(blue.contains(oofEvent.getPlayer())) {
            if(redBannerHolder == oofEvent.getPlayer()) {
                redBannerHolder = null;
                oofEvent.getPlayer().getInventory().setHelmet(blueHat);
                Bukkit.broadcastMessage(oofEvent.getPlayer().getDisplayName() + "  was killed. The Red Banner was returned to its base.");
                setupRedBanner();
                for(Player player : red) {
                    Misc.sendTitle(player, ChatColor.RED + "Banner Returned!", 40);
                }
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    oofEvent.getPlayer().teleport(getLocation("BlueSpawn"));
                }
            }.runTaskLater(PitSim.INSTANCE, 1L);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if(PitEventManager.activeEvent != this) return;
        helmets.put(event.getPlayer(), event.getPlayer().getInventory().getHelmet());

        if(red.size() > blue.size()) {
            blue.add(event.getPlayer());
            event.getPlayer().getInventory().setHelmet(blueHat);
            new BukkitRunnable() {
                @Override
                public void run() {
                    event.getPlayer().teleport(getLocation("BlueSpawn"));
                }
            }.runTaskLater(PitSim.INSTANCE, 1L);
            event.getPlayer().teleport(getLocation("BlueSpawn"));
        }
        if(red.size() < blue.size()) {
            red.add(event.getPlayer());
            event.getPlayer().getInventory().setHelmet(redHat);
            new BukkitRunnable() {
                @Override
                public void run() {
                    event.getPlayer().teleport(getLocation("RedSpawn"));
                }
            }.runTaskLater(PitSim.INSTANCE, 1L);
        }
        if(red.size() == blue.size()) {
            blue.add(event.getPlayer());
            event.getPlayer().getInventory().setHelmet(blueHat);
            new BukkitRunnable() {
                @Override
                public void run() {
                    event.getPlayer().teleport(getLocation("BlueSpawn"));
                }
            }.runTaskLater(PitSim.INSTANCE, 1L);
        }
        captures.put(event.getPlayer(), 0);

    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        if(red.contains(event.getPlayer())) {
            red.remove(event.getPlayer());
            if(blueBannerHolder == event.getPlayer()) {
                blueBannerHolder = null;
                Bukkit.broadcastMessage(event.getPlayer().getDisplayName() + "  has left. The Blue Banner was returned to its base.");
                setupBlueBanner();
                for(Player player : blue) {
                    Misc.sendTitle(player, ChatColor.BLUE + "Banner Returned!", 40);
                }
            }
        }
        if(blue.contains(event.getPlayer())) {
            blue.remove(event.getPlayer());
            if(redBannerHolder == event.getPlayer()) {
                redBannerHolder = null;
                Bukkit.broadcastMessage(event.getPlayer().getDisplayName() + "  has left. The Red Banner was returned to its base.");
                setupRedBanner();
                for(Player player : red) {
                    Misc.sendTitle(player, ChatColor.RED + "Banner Returned!", 40);
                }
            }
        }
        if(helmets.containsKey(event.getPlayer())) {
            event.getPlayer().getInventory().setHelmet(helmets.get(event.getPlayer()));
        } else event.getPlayer().getInventory().setHelmet(new ItemStack(Material.AIR));
        helmets.remove(event.getPlayer());
        captures.remove(event.getPlayer());
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if(PitEventManager.activeEvent != this) return;
        if(event.getSlot() == 39) event.setCancelled(true);
    }

    public void teamSetup() {

        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());

        for(Player player : players) {
            captures.put(player, 0);
        }

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
        blueHat = blueItem;

        for(Player player : blue) {
            if(!Misc.isAirOrNull(player.getInventory().getHelmet())) {
                helmets.put(player, player.getInventory().getHelmet());
            }
            player.getInventory().setHelmet(blueItem);
            player.teleport(getLocation("BlueSpawn"));
            ArmorStand blueStand = (ArmorStand) Bukkit.getWorld("pit").spawnEntity(getLocation("BlueBanner"), EntityType.ARMOR_STAND);
            blueStand.setVisible(false);
            blueStand.setGravity(false);
            blueStand.setCustomNameVisible(true);
            blueStand.setCustomName(ChatColor.BLUE + "" + ChatColor.BOLD + "Blue Flag");
            blueArmor = blueStand;
            setupBlueBanner();
        }

        ItemStack redItem = new ItemStack(Material.WOOL, 1, (short) 14);
        ItemMeta redMeta = redItem.getItemMeta();
        redMeta.setDisplayName(ChatColor.RED + "Red Team");
        redItem.setItemMeta(redMeta);
        redHat = redItem;


        for(Player player : red) {
            if(!Misc.isAirOrNull(player.getInventory().getHelmet())) {
                helmets.put(player, player.getInventory().getHelmet());
            }
            player.getInventory().setHelmet(redItem);
            player.teleport(getLocation("RedSpawn"));
            ArmorStand redStand = (ArmorStand) Bukkit.getWorld("pit").spawnEntity(getLocation("RedBanner"), EntityType.ARMOR_STAND);
            redStand.setVisible(false);
            redStand.setCustomNameVisible(true);
            redStand.setGravity(false);
            redStand.setCustomName(ChatColor.RED + "" + ChatColor.BOLD + "Red Flag");
            redArmor = redStand;
            setupRedBanner();
        }
    }

    public void setupBlueBanner() {
        getLocation("BlueBanner").getBlock().setType(Material.STANDING_BANNER, true);
        Banner blueBanner = (Banner) getLocation("BlueBanner").getBlock().getState();
        blueBanner.setBaseColor(DyeColor.BLUE);
        org.bukkit.material.Banner blueBannerData = (org.bukkit.material.Banner) blueBanner.getData();
        blueBannerData.setFacingDirection(BlockFace.NORTH);
        blueBanner.setData(blueBannerData);
        blueBanner.update();
        blueArmor.setCustomName(ChatColor.BLUE + "" + ChatColor.BOLD + "Blue Flag");
    }

    public void setupRedBanner() {
        getLocation("RedBanner").getBlock().setType(Material.STANDING_BANNER, true);
        Banner redBanner = (Banner) getLocation("RedBanner").getBlock().getState();
        redBanner.setBaseColor(DyeColor.RED);
        org.bukkit.material.Banner redBannerData = (org.bukkit.material.Banner) redBanner.getData();
        redBannerData.setFacingDirection(BlockFace.SOUTH);
        redBanner.setData(redBannerData);
        redBanner.update();
        redArmor.setCustomName(ChatColor.RED + "" + ChatColor.BOLD + "Red Flag");
    }
}
