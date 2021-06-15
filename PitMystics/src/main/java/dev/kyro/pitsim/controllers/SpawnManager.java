package dev.kyro.pitsim.controllers;

import com.sk89q.worldguard.bukkit.BukkitUtil;
import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.events.VolleyShootEvent;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class SpawnManager implements Listener {

    public static List<Arrow> arrowList = new ArrayList<>();


    @EventHandler
    public void onShoot(EntityShootBowEvent event) {

        if(!(event.getEntity() instanceof Player)) return;
        if(!(event.getProjectile() instanceof Arrow)) return;

        Player player = (Player) event.getEntity();

        Location loc = new Location(player.getWorld(), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ());
        RegionContainer container = WorldGuardPlugin.inst().getRegionContainer();
        RegionManager regions = container.get(loc.getWorld());
        assert regions != null;
        ApplicableRegionSet set = regions.getApplicableRegions((BukkitUtil.toVector(loc)));



        for(ProtectedRegion region : set) {
            if(region.getId().equals("spawn")) {
                event.setCancelled(true);
                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1 ,1);
            }
        }


//        arrowList.add((Arrow) event.getProjectile());

    }


//    @EventHandler
//    public void onHit(ProjectileHitEvent event) {
//
//
//        if(!(event.getEntity() instanceof Arrow)) return;
//
//        if(!arrowList.contains((Arrow) event.getEntity())) System.out.println("Arrows are being created that haven't been shot! Please fix this issue.");
//        else arrowList.remove((Arrow) event.getEntity());
//    }
//
//    @EventHandler
//    public void volleyShoot(VolleyShootEvent event) {
//        arrowList.add((Arrow) event.getProjectile());
//
//        Player player = (Player) event.getEntity();
//
//        Location loc = new Location(player.getWorld(), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ());
//        RegionContainer container = WorldGuardPlugin.inst().getRegionContainer();
//        RegionManager regions = container.get(loc.getWorld());
//        assert regions != null;
//        ApplicableRegionSet set = regions.getApplicableRegions((BukkitUtil.toVector(loc)));
//
//        for(ProtectedRegion region : set) {
//            if(region.getId().equals("spawn")) {
//                event.setCancelled(true);
//            }
//
//        }
//    }


//
//    static {
//
//        try {
//
//            new BukkitRunnable() {
//                @Override
//                public void run() {
//
//                    for(Iterator<Arrow> iterator = arrowList.iterator(); arrowList.iterator().hasNext(); ) {
//                        Arrow arrow = iterator.next();
//
//                        Location loc = new Location(arrow.getWorld(), arrow.getLocation().getX(), arrow.getLocation().getY(), arrow.getLocation().getZ());
//                        RegionContainer container = WorldGuardPlugin.inst().getRegionContainer();
//                        RegionManager regions = container.get(loc.getWorld());
//                        assert regions != null;
//                        ApplicableRegionSet set = regions.getApplicableRegions((BukkitUtil.toVector(loc)));
//
//                        for(ProtectedRegion region : set) {
//                            if(region.getId().equals("spawn")) {
//                                arrowList.remove(arrow);
//                                arrow.remove();
//                            }
//
//                        }
//
//
//                    }
//
//                }
//            }.runTaskTimer(PitSim.INSTANCE, 0L, 10L);
//        } catch(Exception e) {
//
//        }
//    }

}
