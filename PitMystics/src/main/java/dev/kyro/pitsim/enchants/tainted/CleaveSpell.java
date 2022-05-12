package dev.kyro.pitsim.enchants.tainted;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.Cooldown;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.events.OofEvent;
import dev.kyro.pitsim.events.PitPlayerAttemptAbilityEvent;
import dev.kyro.pitsim.misc.Sounds;
import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntity;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.*;

public class CleaveSpell extends PitEnchant {

    public static Map<UUID, ArmorStand> stands = new HashMap<>();
    public static int i;

    static {
        List<UUID> remove = new ArrayList<>();
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<UUID, ArmorStand> entry : stands.entrySet()) {
                    entry.getValue().setVelocity(entry.getValue().getVelocity().clone().setY(0));
                    if(entry.getValue().getVelocity().getX() == 0) remove.add(entry.getKey());
                    for (Entity nearbyEntity : entry.getValue().getNearbyEntities(40, 40, 40)) {
                        if(!(nearbyEntity instanceof Player)) continue;
                        Player player = (Player) nearbyEntity;

                        PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook identityTpPacket = new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(getStandID(entry.getValue()), (byte) 0, (byte) 0, (byte) 0, (byte) i, (byte) 0, false);
                        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(identityTpPacket);

                        i += 32;
                        if(i >= 256) i = 0;
                    }
                    for (Entity nearbyEntity : entry.getValue().getNearbyEntities(0.5, 0.5, 0.5)) {
                        if(nearbyEntity instanceof ArmorStand) continue;
                        if(nearbyEntity instanceof Villager) continue;
                        if(!(nearbyEntity instanceof LivingEntity)) continue;
                        Player player = null;
                        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                            if(onlinePlayer.getUniqueId().equals(entry.getKey())) player = onlinePlayer;
                        }
                        if(player == null) return;
                        if(nearbyEntity == player) continue;
                        EntityDamageByEntityEvent damageEvent = new EntityDamageByEntityEvent(player, nearbyEntity, EntityDamageEvent.DamageCause.ENTITY_ATTACK, 13);
                        damageEvent.setDamage(15);
                        Bukkit.getServer().getPluginManager().callEvent(damageEvent);
                        Sounds.CLEAVE3.play(player);
                    }
                }
                for (UUID id : remove) {
                    Player player = null;
                    for (Player online : Bukkit.getOnlinePlayers()) {
                        if(online.getUniqueId().equals(id)) player = online;
                    }
                    if(player == null) continue;
                    stands.get(id).remove();
                    stands.remove(id);
                    Sounds.CLEAVE2.play(player);
                }
                remove.clear();
            }
        }.runTaskTimer(PitSim.INSTANCE, 2, 2);
    }

    public CleaveSpell() {
        super("Cleave", true, ApplyType.SCYTHES, "cleave", "cleaver", "saving", "grace");
        tainted = true;
    }

    @EventHandler
    public void onUse(PitPlayerAttemptAbilityEvent event) {
        int enchantLvl = event.getEnchantLevel(this);
        if(enchantLvl == 0) return;

        Cooldown cooldown = getCooldown(event.getPlayer(), 10);
        if(cooldown.isOnCooldown()) return;

        PitPlayer pitPlayer = PitPlayer.getPitPlayer(event.getPlayer());
        if(!pitPlayer.useMana(getManaCost(enchantLvl))) {
            Sounds.NO.play(event.getPlayer());
            return;
        }

        pitPlayer.updateMaxHealth();
        if(event.getPlayer().getMaxHealth() <= 4) {
            AOutput.send(event.getPlayer(), "&c&lNOPE! &7Not enough health!");
            Sounds.NO.play(event.getPlayer());
            return;
        }

        cooldown.restart();

        Player player = event.getPlayer();
        ArmorStand stand = (ArmorStand) player.getWorld().spawnEntity(player.getLocation().add(0, 1, 0), EntityType.ARMOR_STAND);
        stands.put(player.getUniqueId(), stand);
        stand.setGravity(true);
        Vector vector = player.getTargetBlock((Set<Material>) null, 30).getLocation().toVector().subtract(player.getLocation().add(0, 1, 0).toVector()).setY(2).multiply(0.1);

        for (Entity entity : player.getNearbyEntities(15, 15, 15)) {
            Vector direction = player.getLocation().getDirection();
            Vector towardsEntity = entity.getLocation().subtract(player.getLocation()).toVector().normalize();

            if(direction.distance(towardsEntity) < 0.3) {
                vector = entity.getLocation().toVector().subtract(player.getLocation().add(0, 1, 0).toVector()).setY(2).normalize().multiply(1.5);
            }
        }

        stand.setArms(true);
        stand.setVisible(false);
        stand.setSmall(true);
        stand.setItemInHand(player.getItemInHand().clone());
        stand.setRightArmPose(new EulerAngle(Math.toRadians(0), Math.toRadians(345), Math.toRadians(318)));
        ((EntityArmorStand) ((CraftEntity) stand).getHandle()).setGravity(false);

        stand.setVelocity(vector);
        Sounds.CLEAVE1.play(player);


    }

    @EventHandler
    public void onKill(KillEvent event) {
        if(!event.deadIsPlayer) return;
        PitPlayer.getPitPlayer(event.deadPlayer).graceTiers = 0;
        PitPlayer.getPitPlayer(event.deadPlayer).heal(event.deadPlayer.getMaxHealth());
    }

    @EventHandler
    public void onOof(OofEvent event) {
        PitPlayer.getPitPlayer(event.getPlayer()).graceTiers = 0;
        PitPlayer.getPitPlayer(event.getPlayer()).heal(event.getPlayer().getMaxHealth());
    }

    @Override
    public List<String> getDescription(int enchantLvl) {
        return new ALoreBuilder("&7Throw your scythe, dealing damage", "&7to all it hits", "&d&oCosts " + getManaCost(enchantLvl) + " Mana").getLore();
    }

    public static int getStandID(final ArmorStand stand) {
        for (Entity entity : stand.getWorld().getNearbyEntities(stand.getLocation(), 5.0, 5.0, 5.0)) {
            if (!(entity instanceof ArmorStand)) continue;
            if (entity.getUniqueId().equals(stand.getUniqueId())) return entity.getEntityId();
        }
        return 0;
    }

    public static int getManaCost(int enchantLvl) {
        return 30 * (4 - enchantLvl);
    }
}
