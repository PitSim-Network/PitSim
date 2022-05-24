package dev.kyro.pitsim.enchants.tainted;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.enums.MysticType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class Laser extends PitEnchant {
    public Laser() {
        super("Laser", true, ApplyType.CHESTPLATES, "laser", "las", "lazer");
        tainted = true;
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        int enchantLvl = EnchantManager.getEnchantLevel(event.getPlayer(), this);
        if(enchantLvl == 0) return;
        if(!MapManager.inDarkzone(event.getPlayer())) return;

        if(event.getAction() != Action.LEFT_CLICK_AIR) return;
        if(Misc.isAirOrNull(event.getPlayer().getItemInHand())) return;
        MysticType type = MysticType.getMysticType(event.getPlayer().getItemInHand());
        if(type != MysticType.TAINTED_SCYTHE) return;
        Player player = event.getPlayer();

        LivingEntity attacked;
        Set<Material> mat = null;
        Block block = player.getTargetBlock(mat, 20);

        for (Entity entity : player.getNearbyEntities(20, 20, 20)) {
            if(!(entity instanceof LivingEntity)) continue;
            if(entity instanceof ArmorStand || entity instanceof Villager) continue;

            Vector direction = player.getLocation().add(0, 1, 0).getDirection();
            Vector towardsEntity = entity.getLocation().subtract(player.getLocation().add(0, 1, 0)).toVector().normalize();

            if(direction.distance(towardsEntity) < 0.1) {
                attacked = (LivingEntity) entity;
                block = entity.getLocation().getBlock();

                EntityDamageByEntityEvent damageEvent = new EntityDamageByEntityEvent(player, attacked, EntityDamageEvent.DamageCause.ENTITY_ATTACK, 13);
                Bukkit.getServer().getPluginManager().callEvent(damageEvent);
                if(!damageEvent.isCancelled()) attacked.damage(13, player);
            }
        }

        Vector diff = block.getLocation().add(0.5, 1, 0.5).subtract(player.getLocation().add(0, 1, 0)).toVector();
        Location base = player.getLocation().add(0, 1, 0)/* the origin, where you are moving away from */;
        double add = diff.length(); //example amount
        diff.divide(new Vector(add, add, add));

        for (int i = 0; i < add; i++) {
            base.add(diff);
            base.getWorld().spigot().playEffect(base, Effect.COLOURED_DUST, 0, 0, (float) 0, (float) 0/255, (float) 0/255, 1, 0, 64);
        }

    }

    @Override
    public List<String> getDescription(int enchantLvl) {
        return new ALoreBuilder("&7Your melee attacks become ranged", "&7with &f+20 blocks &7of reach", "&d&o-" + reduction(enchantLvl) + "% Mana Regen").getLore();
    }

    public static int reduction(int enchantLvl) {
        return 80 - (20 * enchantLvl);
    }
}
