package dev.kyro.pitsim.enchants.tainted;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.BossManager;
import dev.kyro.pitsim.controllers.Cooldown;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.PitPlayerAttemptAbilityEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Set;

public class ExtractSpell extends PitEnchant {



    public ExtractSpell() {
        super("Extract", true, ApplyType.SCYTHES, "extract", "ext");
        tainted = true;
    }

    @EventHandler
    public void onUse(PitPlayerAttemptAbilityEvent event) {
        int enchantLvl = event.getEnchantLevel(this);
        if(enchantLvl == 0) return;

        Cooldown cooldown = getCooldown(event.getPlayer(), 10);
        if(cooldown.isOnCooldown()) return;

        Player player = event.getPlayer();
        Vector vector = player.getTargetBlock((Set<Material>) null, 30).getLocation().toVector().subtract(player.getLocation().add(0, 1, 0).toVector()).setY(2).multiply(0.1);
        LivingEntity pullEntity = null;

        for (Entity entity : player.getNearbyEntities(15, 15, 15)) {
            if(!(entity instanceof LivingEntity)) continue;
            if(entity instanceof ArmorStand) continue;
            Vector direction = player.getLocation().getDirection();
            Vector towardsEntity = entity.getLocation().subtract(player.getLocation()).toVector().normalize();

            if(direction.distance(towardsEntity) < 0.3) {
                vector = player.getLocation().toVector().subtract(entity.getLocation().add(0, 1, 0).toVector()).setY(2).normalize().multiply(2);
                pullEntity = (LivingEntity) entity;

                for (Entity nearbyEntity : entity.getNearbyEntities(10, 10, 10)) {
                    if(!(nearbyEntity instanceof LivingEntity)) continue;
                    if(nearbyEntity instanceof ArmorStand) continue;
                    if(((LivingEntity) nearbyEntity).getHealth() > ((LivingEntity) entity).getHealth()) {
                        pullEntity = (LivingEntity) nearbyEntity;
                        vector = player.getLocation().toVector().subtract(nearbyEntity.getLocation().add(0, 1, 0).toVector()).setY(2).normalize().multiply(2);
                    }
                }
            }
        }

        if(pullEntity == null) {
            AOutput.send(player, "&c&lNOPE! &7No target found!");
            Sounds.NO.play(player);
            return;
        }

        PitPlayer pitPlayer = PitPlayer.getPitPlayer(event.getPlayer());
        if(!pitPlayer.useMana(getManaCost(enchantLvl))) {
            Sounds.NO.play(event.getPlayer());
            return;
        }

        cooldown.restart();

        Sounds.EXTRACT.play(player);
        Vector diff = player.getLocation().add(0, 1, 0).subtract(pullEntity.getLocation()).toVector();
        Location base = pullEntity.getLocation(); /* the origin, where you are moving away from */;
        double add = diff.length(); //example amount
        diff.divide(new Vector(add, add, add));

        for (int i = 0; i < add; i++) {
            base.add(diff);
            base.getWorld().playEffect(base, Effect.LAVADRIP, 10, 10);
        }

        pullEntity.setVelocity(vector);

    }

    @Override
    public List<String> getDescription(int enchantLvl) {
        return new ALoreBuilder("&7Pull the most powerful", "&7mob out of a crowd", "&d&o-" + getManaCost(enchantLvl) + " Mana").getLore();
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
