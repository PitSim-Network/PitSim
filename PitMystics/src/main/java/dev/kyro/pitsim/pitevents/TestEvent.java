package dev.kyro.pitsim.pitevents;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.schematic.MCEditSchematicFormat;
import com.sk89q.worldedit.world.DataException;
import dev.kyro.arcticapi.misc.ASound;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.LeaderboardManager;
import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.PitEventManager;
import dev.kyro.pitsim.controllers.objects.PitEvent;
import dev.kyro.pitsim.enums.GameMap;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.events.OofEvent;
import dev.kyro.pitsim.misc.Misc;
import me.clip.placeholderapi.PlaceholderAPI;
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
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
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
        super("Capture The Flag", 5, true, ChatColor.GREEN);
        INSTANCE = this;
    }

    @Override
    public String getName() {return "Capture The Flag";}

    @Override
    public void prepare() {
    }

    @Override
    public void start() {
        teamSetup();
        PitEventManager.activeEvent = this;

    }

    @Override
    public void end() {
        for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if(helmets.containsKey(onlinePlayer)) onlinePlayer.getInventory().setHelmet(helmets.get(onlinePlayer));
            else onlinePlayer.getInventory().setHelmet(new ItemStack(Material.AIR));
            helmets.remove(onlinePlayer);
            onlinePlayer.playEffect(getLocation("BlueBanner"), Effect.RECORD_PLAY,0);
        }
        redArmor.remove();
        blueArmor.remove();
        redBannerHolder = null;
        blueBannerHolder = null;
        getLocation("BlueBanner").getBlock().setType(Material.AIR, true);
        getLocation("RedBanner").getBlock().setType(Material.AIR, true);
        if(MapManager.map == GameMap.DESERT) loadSchematic(new File("plugins/WorldEdit/schematics/CTFResetDesert.schematic"), getLocation("BlueSchematic"));
        if(MapManager.map == GameMap.DESERT) loadSchematic(new File("plugins/WorldEdit/schematics/CTFResetDesert.schematic"), getLocation("RedSchematic"));
        getTopThree();
        red.clear();
        blue.clear();
        captures.clear();
        PitEventManager.activeEvent = null;
    }

    public void getTopThree() {
        Map<String, Integer> stringPlayers = new HashMap<>();
        String playerOne = null;
        String playerTwo = null;
        String playerThree = null;
        String messageOne = null;
        String messageTwo = null;
        String messageThree = null;
        Player player1 = null;
        Player player2 = null;
        Player player3 = null;

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
            if(player.getDisplayName().equalsIgnoreCase(playerOne)) {
                if(red.contains(player)) messageOne = ChatColor.translateAlternateColorCodes('&',
                        "   &e&l#1 %luckperms_prefix%" + player.getDisplayName() + " &ewith &c" + captures.get(player) + " &cCaptures");
                else messageOne = ChatColor.translateAlternateColorCodes('&',
                        "   &e&l#1 %luckperms_prefix%" + player.getDisplayName() + " &ewith &9" + captures.get(player) + " &9Captures");
                player1 = player;
            }
            if(player.getDisplayName().equalsIgnoreCase(playerTwo)) {
                if(red.contains(player)) messageTwo = ChatColor.translateAlternateColorCodes('&',
                        "   &e&l#2 %luckperms_prefix%" + player.getDisplayName() + " &ewith &c" + captures.get(player) + " &cCaptures");
                else messageTwo = ChatColor.translateAlternateColorCodes('&',
                        "   &e&l#2 %luckperms_prefix%" + player.getDisplayName() + " &ewith &9" + captures.get(player) + " &9Captures");
                player2 = player;
            }
            if(player.getDisplayName().equalsIgnoreCase(playerThree)) {
                if(red.contains(player)) messageThree = ChatColor.translateAlternateColorCodes('&',
                        "   &e&l#3 %luckperms_prefix%" + player.getDisplayName() + " &ewith &c" + captures.get(player) + " &cCaptures");
                else messageThree = ChatColor.translateAlternateColorCodes('&',
                        "   &e&l#3 %luckperms_prefix%" + player.getDisplayName() + " &ewith &9" + captures.get(player) + " &9Captures");
                player3 = player;
            }
        }

        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6&m------------------------"));
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6&lPIT EVENT ENDED: " +
                this.color + "" + ChatColor.BOLD + this.getName().toUpperCase(Locale.ROOT) + "&6&l!"));
        for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if(red.contains(onlinePlayer)) onlinePlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lYou: &a" + captures.get(onlinePlayer) + " &cCaptures"));
            else onlinePlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lYou: &9" + captures.get(onlinePlayer) + " &9Captures"));
        }
        if(messageOne != null) Bukkit.broadcastMessage(PlaceholderAPI.setPlaceholders(player1, messageOne));
        if(messageTwo != null) Bukkit.broadcastMessage(PlaceholderAPI.setPlaceholders(player2, messageTwo));
        if(messageThree != null) Bukkit.broadcastMessage(PlaceholderAPI.setPlaceholders(player3, messageThree));
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6&m------------------------"));
    }

    public Location getLocation(String location) {
        if(location == "BlueSpawn") {
            if(MapManager.map == GameMap.DESERT) return new Location(Bukkit.getWorld("pit"), -177, 46, 148);
        }
        if(location == "BlueBanner") {
            if(MapManager.map == GameMap.DESERT) return new Location(Bukkit.getWorld("pit"), -168, 48, 158);
        }
        if(location == "BlueSchematic") {
            if(MapManager.map == GameMap.DESERT) return new Location(Bukkit.getWorld("pit"), -168, 46, 158);
        }
        if(location == "RedSchematic") {
            if(MapManager.map == GameMap.DESERT) return new Location(Bukkit.getWorld("pit"), -84, 45, 264);
        }
        if(location == "RedSpawn") {
            if(MapManager.map == GameMap.DESERT) return new Location(Bukkit.getWorld("pit"), -75, 45, 269);
        }
        if(location == "RedBanner") {
            if(MapManager.map == GameMap.DESERT) return new Location(Bukkit.getWorld("pit"), -84, 47, 264);
        }
        return null;
    }

    @EventHandler
    public void onEntityAttack(EntityDamageByEntityEvent event) {
        if(event.getEntity() instanceof ArmorStand) event.setCancelled(true);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if(!PitEventManager.majorEvent || PitEventManager.activeEvent != this) return;
        if(blueArmor !=  null) {
            List<Entity> blueBannerPlayers = blueArmor.getNearbyEntities(2, 2, 2);
            for(Entity blueBannerPlayer : blueBannerPlayers) {
                if(blueBannerPlayer instanceof Player && !blue.contains(blueBannerPlayer) && blueBannerHolder == null) {
                    String message = ChatColor.translateAlternateColorCodes('&', ChatColor.BLUE +
                            "" + ChatColor.BOLD + "BLUE FLAG! &7Stolen by %luckperms_prefix%") + ((Player) blueBannerPlayer).getDisplayName() + "&7!";
                    Bukkit.broadcastMessage(PlaceholderAPI.setPlaceholders((Player) blueBannerPlayer, message));
                    blueBannerHolder = (Player) blueBannerPlayer;
                    blueBannerHolder.getInventory().setHelmet(new ItemStack(Material.BANNER, 1, (short) 4));
                    getLocation("BlueBanner").getBlock().setType(Material.AIR);
                    blueArmor.setCustomName(ChatColor.BLUE + "Blue Banner Taken by " + ((Player) blueBannerPlayer).getDisplayName());
                    explosion(blueArmor, (Player) blueBannerPlayer);
                    Misc.sendTitle((Player) blueBannerPlayer, ChatColor.RED + "You Got The Flag!", 40);
                    Misc.sendSubTitle((Player) blueBannerPlayer, ChatColor.RED + "Return it to your base", 40);
                    ASound.play((Player) blueBannerPlayer, Sound.NOTE_PLING, 2, 2F);
                    for(Player player : blue) {
                        Misc.sendTitle(player, ChatColor.BLUE + "Flag Stolen!", 40);
                        ASound.play(player, Sound.NOTE_PLING, 2, 0.5F);
                    }
                }
                if(blueBannerPlayer instanceof Player && blue.contains(blueBannerPlayer) && redBannerHolder == blueBannerPlayer) {
                    ((Player) blueBannerPlayer).getInventory().setHelmet(blueHat);
                    captures.put((Player) blueBannerPlayer, captures.get(blueBannerPlayer) + 1);
                    redBannerHolder  = null;
                    setupRedBanner();
                    explosion(blueArmor, (Player) blueBannerPlayer);
                    String message = ChatColor.translateAlternateColorCodes('&', ChatColor.RED +
                            "" + ChatColor.BOLD + "RED FLAG! &7Captured by %luckperms_prefix%") + ((Player) blueBannerPlayer).getDisplayName() + "&7!";
                    Bukkit.broadcastMessage(PlaceholderAPI.setPlaceholders((Player) blueBannerPlayer, message));
                    for(Player player : red) {
                        Misc.sendTitle(player, ChatColor.RED + "Flag Captured!", 40);
                        ASound.play(player, Sound.BLAZE_DEATH, 2, 0.5F);
                    }
                    for(Player player : blue) {
                        Misc.sendTitle(player, ChatColor.BLUE + "Your Team Scored!", 40);
                        ASound.play(player, Sound.LEVEL_UP, 2, 0.5F);
                    }
                }
            }
        }
        if(redArmor !=  null) {
            List<Entity> redBannerPlayers = redArmor.getNearbyEntities(2, 2, 2);
            for(Entity redBannerPlayer : redBannerPlayers) {
                if(redBannerPlayer instanceof Player && !red.contains(redBannerPlayer) && redBannerHolder == null) {
                    String message = ChatColor.translateAlternateColorCodes('&', ChatColor.RED +
                            "" + ChatColor.BOLD + "RED FLAG! &7Stolen by %luckperms_prefix%") + ((Player) redBannerPlayer).getDisplayName() + "&7!";
                    Bukkit.broadcastMessage(PlaceholderAPI.setPlaceholders((Player) redBannerPlayer, message));
                    redBannerHolder = (Player) redBannerPlayer;
                    redBannerHolder.getInventory().setHelmet(new ItemStack(Material.BANNER, 1, (short) 1));
                    getLocation("RedBanner").getBlock().setType(Material.AIR);
                    redArmor.setCustomName(ChatColor.RED + "Red Banner Taken by " + ((Player) redBannerPlayer).getDisplayName());
                    explosion(redArmor, (Player) redBannerPlayer);
                    Misc.sendTitle((Player) redBannerPlayer, ChatColor.BLUE + "You Got The Flag!", 40);
                    Misc.sendSubTitle((Player) redBannerPlayer, ChatColor.BLUE + "Return it to your base", 40);
                    ASound.play((Player) redBannerPlayer, Sound.NOTE_PLING, 2, 2F);
                    for(Player player : red) {
                        Misc.sendTitle(player, ChatColor.RED + "Flag Stolen!", 40);
                        ASound.play(player, Sound.NOTE_PLING, 2, 0.5F);
                    }
                }
                if(redBannerPlayer instanceof Player && red.contains(redBannerPlayer) && blueBannerHolder == redBannerPlayer) {
                    ((Player) redBannerPlayer).getInventory().setHelmet(redHat);
                    captures.put((Player) redBannerPlayer, captures.get(redBannerPlayer) + 1);
                    blueBannerHolder = null;
                    explosion(redArmor, (Player) redBannerPlayer);
                    setupBlueBanner();
                    String message = ChatColor.translateAlternateColorCodes('&', ChatColor.BLUE +
                            "" + ChatColor.BOLD + "BLUE FLAG! &7Captured by %luckperms_prefix%") + ((Player) redBannerPlayer).getDisplayName() + "&7!";
                    Bukkit.broadcastMessage(PlaceholderAPI.setPlaceholders((Player) redBannerPlayer, message));
                    for(Player player : blue) {
                        Misc.sendTitle(player, ChatColor.BLUE + "Flag Captured!", 40);
                        ASound.play(player, Sound.BLAZE_DEATH, 2, 0.5F);
                    }
                    for(Player player : red) {
                        Misc.sendTitle(player, ChatColor.RED + "Your Team Scored!", 40);
                        ASound.play(player, Sound.LEVEL_UP, 2, 0.5F);
                    }
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
                String message = ChatColor.translateAlternateColorCodes('&',
                        "&9&lBLUE FLAG! &7Returned because %luckperms_prefix%" + killEvent.dead.getDisplayName() + " &7was killed.");
                Bukkit.broadcastMessage(PlaceholderAPI.setPlaceholders(killEvent.dead, message));
                setupBlueBanner();
                for(Player player : blue) {
                    Misc.sendTitle(player, ChatColor.BLUE + "Flag Returned!", 40);
                    ASound.play(player, Sound.NOTE_PLING, 2, 1F);
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
                String message = ChatColor.translateAlternateColorCodes('&',
                        "&c&lRED FLAG! &7Returned because %luckperms_prefix%" + killEvent.dead.getDisplayName() + " &7was killed.");
                Bukkit.broadcastMessage(PlaceholderAPI.setPlaceholders(killEvent.dead, message));
                setupRedBanner();
                for(Player player : red) {
                    Misc.sendTitle(player, ChatColor.RED + "Flag Returned!", 40);
                    ASound.play(player, Sound.NOTE_PLING, 2, 1F);
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
                String message = ChatColor.translateAlternateColorCodes('&',
                        "&9&lBLUE FLAG! &7Returned because %luckperms_prefix%" + oofEvent.getPlayer().getDisplayName() + " &7was killed.");
                Bukkit.broadcastMessage(PlaceholderAPI.setPlaceholders(oofEvent.getPlayer(), message));
                setupBlueBanner();
                for(Player player : blue) {
                    Misc.sendTitle(player, ChatColor.BLUE + "Flag Returned!", 40);
                    ASound.play(player, Sound.NOTE_PLING, 2, 1F);
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
                String message = ChatColor.translateAlternateColorCodes('&',
                        "&c&lRED FLAG! &7Returned because %luckperms_prefix%" + oofEvent.getPlayer().getDisplayName() + " &7was killed.");
                Bukkit.broadcastMessage(PlaceholderAPI.setPlaceholders(oofEvent.getPlayer(), message));
                setupRedBanner();
                for(Player player : red) {
                    Misc.sendTitle(player, ChatColor.RED + "Flag Returned!", 40);
                    ASound.play(player, Sound.NOTE_PLING, 2, 1F);
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
                String message = ChatColor.translateAlternateColorCodes('&',
                        "&9&lBLUE FLAG! &7Returned because %luckperms_prefix%" + event.getPlayer().getDisplayName() + " &7has left.");
                Bukkit.broadcastMessage(PlaceholderAPI.setPlaceholders(event.getPlayer(), message));
                setupBlueBanner();
                for(Player player : blue) {
                    Misc.sendTitle(player, ChatColor.BLUE + "Flag Returned!", 40);
                    ASound.play(player, Sound.NOTE_PLING, 2, 1F);
                }
            }
        }
        if(blue.contains(event.getPlayer())) {
            blue.remove(event.getPlayer());
            if(redBannerHolder == event.getPlayer()) {
                redBannerHolder = null;
                String message = ChatColor.translateAlternateColorCodes('&',
                        "&c&lRED FLAG! &7Returned because %luckperms_prefix%" + event.getPlayer().getDisplayName() + " &7was killed.");
                Bukkit.broadcastMessage(PlaceholderAPI.setPlaceholders(event.getPlayer(), message));
                setupRedBanner();
                for(Player player : red) {
                    Misc.sendTitle(player, ChatColor.RED + "Flag Returned!", 40);
                    ASound.play(player, Sound.NOTE_PLING, 2, 1F);
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

    @EventHandler
    public void onAttack(AttackEvent.Pre attackEvent) {
        if(red.contains(attackEvent.attacker) && red.contains(attackEvent.defender)) attackEvent.setCancelled(true);
        if(blue.contains(attackEvent.attacker) && blue.contains(attackEvent.defender)) attackEvent.setCancelled(true);
    }

    public void teamSetup() {

        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());


        for(Player player : players) {
            captures.put(player, 0);
            player.playEffect(MapManager.getMid(), Effect.RECORD_PLAY, Material.RECORD_4.getId());
//            player.playEffect(getLocation("BlueBanner"), Effect.RECORD_PLAY, Material.RECORD_3.getId());
//            player.playEffect(getLocation("RedBanner"), Effect.RECORD_PLAY, Material.RECORD_3.getId());
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
            loadSchematic(new File("plugins/WorldEdit/schematics/BluePoint.schematic"), getLocation("BlueSchematic"));
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
            loadSchematic(new File("plugins/WorldEdit/schematics/RedPoint.schematic"), getLocation("RedSchematic"));
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

    private void loadSchematic(File file, Location location) {
        WorldEditPlugin worldEditPlugin = (WorldEditPlugin)Bukkit.getPluginManager().getPlugin("WorldEdit");
        EditSession session = worldEditPlugin.getWorldEdit().getEditSessionFactory().getEditSession(new BukkitWorld(location.getWorld()), 10000);
        try
        {
            CuboidClipboard clipboard = MCEditSchematicFormat.getFormat(file).load(file);
            clipboard.rotate2D(90);
            clipboard.paste(session, new com.sk89q.worldedit.Vector(location.getX(), location.getY(), location.getZ()), false);
        }
        catch (MaxChangedBlocksException | DataException | IOException e)
        {
            e.printStackTrace();
        }
    }

    public void explosion(ArmorStand armorStand, Player bypassPlayer) {
        List<Player> explosionPlayers = new ArrayList<>();
        List<Entity> nearbyPlayers = armorStand.getNearbyEntities(5, 5, 5);
        for(Entity nearbyPlayer : nearbyPlayers) {
            if(nearbyPlayer instanceof Player) explosionPlayers.add((Player) nearbyPlayer);
        }

        for(Player player : explosionPlayers) {
            Vector force = player.getLocation().toVector().subtract(armorStand.getLocation().toVector())
                    .setY(1).normalize().multiply(2);
           if(bypassPlayer != player) player.setVelocity(force);
        }
        armorStand.getLocation().getWorld().playSound(armorStand.getLocation(), Sound.EXPLODE, 1, 2);
        armorStand.getLocation().getWorld().playEffect(armorStand.getLocation(), Effect.EXPLOSION_HUGE,  200, 200);
    }
}
