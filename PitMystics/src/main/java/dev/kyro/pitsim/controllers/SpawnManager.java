package dev.kyro.pitsim.controllers;

import com.sk89q.worldguard.bukkit.BukkitUtil;
import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class SpawnManager implements Listener {

    @EventHandler
    public void onShoot(EntityShootBowEvent event) {
        if(!(event.getEntity() instanceof Player)) return;
        if(!(event.getProjectile() instanceof Arrow)) return;

        Player player = (Player) event.getEntity();

        if(isInSpawn(player.getLocation())) {
            event.setCancelled(true);
            Sounds.NO.play(player);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if(!isInSpawn(event.getItemDrop().getLocation())) return;
        ItemStack dropped = event.getItemDrop().getItemStack();
        NBTItem nbtItem = new NBTItem(dropped);
        if(nbtItem.hasKey(NBTTag.DROP_CONFIRM.getRef())) return;
        if(dropped.getType() == Material.ENDER_CHEST || dropped.getType() == Material.TRIPWIRE_HOOK) return;
        event.getItemDrop().remove();
        AOutput.send(event.getPlayer(), "&c&lITEM DELETED! &7Dropped in spawn area.");
        Sounds.NO.play(event.getPlayer());
    }

    public static Boolean isInSpawn(Location loc) {
        RegionContainer container = WorldGuardPlugin.inst().getRegionContainer();
        RegionManager regions = container.get(loc.getWorld());
        assert regions != null;
        ApplicableRegionSet set = regions.getApplicableRegions((BukkitUtil.toVector(loc)));

        for(ProtectedRegion region : set) {
            if(region.getId().equals("spawn") || region.getId().equals("spawn2")) {
                return true;
            }
        }
        return false;
    }

    public static void clearSpawnStreaks() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(isInSpawn(player.getLocation())) {
                PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
                pitPlayer.endKillstreak();
            }
        }

    }

    static {
        new BukkitRunnable() {
            @Override
            public void run() {
                clearSpawnStreaks();
            }
        }.runTaskTimer(PitSim.INSTANCE, 20L, 20L);
    }
}
