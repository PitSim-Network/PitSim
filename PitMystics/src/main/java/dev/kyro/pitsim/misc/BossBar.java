package dev.kyro.pitsim.misc;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.NonManager;
import net.minecraft.server.v1_8_R3.*;
import net.minecraft.server.v1_8_R3.Entity;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.data.type.Fire;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class BossBar implements Listener {
    public static Map<Player, String> messages = new HashMap<>();
    public static Map<Player, Double> progress = new HashMap<>();
    public static Map<Player, Wither> withers = new HashMap<>();

    static {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : messages.keySet()) {
                    if(!withers.containsKey(player)) spawnWither(player);
                    else moveWither(player);
                }
            }
        }.runTaskTimer(PitSim.INSTANCE, 5, 5);
    }

    public static void setBossBar(Player player, String message) {
        messages.put(player, message);
        progress.put(player, 1D);
    }

    public static void setMessage(Player player, String message) {
        if(messages.containsKey(player)) messages.put(player, message);
    }

    public static void setProgress(Player player, double newProgress) {
        if(progress.containsKey(player)) progress.put(player, newProgress);
    }

    public static void removeBossBar(Player player) {
        messages.remove(player);
        progress.remove(player);
        withers.get(player).remove();
        withers.remove(player);
    }

    public static void spawnWither(Player player) {
        System.out.println("spawned");
        Location location = player.getTargetBlock((HashSet<Byte>) null, 4).getLocation();
        Wither wither = player.getWorld().spawn(location, Wither.class);
//        wither.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 100000, 0, false, false));
        noAI(wither);
        withers.put(player, wither);
    }

    public static void moveWither(Player player) {
        Wither wither = withers.get(player);
        wither.setCustomName(ChatColor.translateAlternateColorCodes('&', messages.get(player)));
        double health = wither.getMaxHealth() * progress.get(player);
        wither.setHealth(health);
        for (org.bukkit.entity.Entity entityWither : player.getWorld().getEntitiesByClasses(Wither.class)) {
            ((LivingEntity) entityWither).addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 60, 0, true, false));
        }
        wither.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 60, 1, true, false));
        Location location = player.getTargetBlock((HashSet<Byte>) null, 5).getLocation();
        wither.teleport(location.add(0, 3, 0));
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        if(withers.containsKey(event.getPlayer())) removeBossBar(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onHit(EntityDamageByEntityEvent event) {
        if(event.getDamager() instanceof Arrow) return;
        if(event.getDamager() instanceof Fireball) return;
        if(NonManager.getNon((LivingEntity) event.getDamager()) != null) return;

        if(!(event.getEntity() instanceof Wither)) return;


        for(Wither wither : withers.values()) {
            if(event.getEntity() == wither) event.setCancelled(true);
        }
    }

    public static void noAI(org.bukkit.entity.Entity bukkitEntity) {
        Entity nmsEntity = ((CraftEntity) bukkitEntity).getHandle();
        NBTTagCompound tag = nmsEntity.getNBTTag();
        if (tag == null) {
            tag = new NBTTagCompound();
        }
        nmsEntity.c(tag);
        tag.setInt("NoAI", 1);
        nmsEntity.f(tag);
    }


}
