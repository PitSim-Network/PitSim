package dev.kyro.pitsim.enchants.tainted;

import com.sk89q.worldedit.EditSession;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.Cooldown;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.PitPlayerAttemptAbilityEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.SchematicPaste;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FreezeSpell extends PitEnchant {
    public static List<EditSession> sessions = new ArrayList<>();
    public static Map<Location, Material> blocks = new HashMap<>();

    public FreezeSpell() {
        super("Freeze", true, ApplyType.SCYTHES, "freeze", "fre", "cold");
        tainted = true;
    }

    @EventHandler
    public void onUse(PitPlayerAttemptAbilityEvent event) {
        int enchantLvl = event.getEnchantLevel(this);
        if(enchantLvl == 0) return;

        Block block = event.getPlayer().getLocation().subtract(0, 1, 0).getBlock();
        if(block.getType().equals(Material.AIR) && event.getPlayer().getLocation().subtract(0, 2, 0).getBlock().getType() == Material.AIR) {
            AOutput.send(event.getPlayer(), "&c&lNOPE! &7Must be standing on a block!");
            Sounds.NO.play(event.getPlayer());
            return;
        }


        Cooldown cooldown = getCooldown(event.getPlayer(), 41);
        if(cooldown.isOnCooldown()) return;

        PitPlayer pitPlayer = PitPlayer.getPitPlayer(event.getPlayer());
        if(!pitPlayer.useMana(getManaCost(enchantLvl))) {
            Sounds.NO.play(event.getPlayer());
            return;
        }

        cooldown.restart();

        Player player = event.getPlayer();
        Location location;
        if(player.getLocation().subtract(0, 1, 0).getBlock().getType() == Material.AIR) location = player.getLocation().subtract(0, 1, 0);
        else location = player.getLocation();

        player.spigot().playEffect(player.getLocation().add(0, 1, 0), Effect.SNOWBALL_BREAK, 1, 1, (float) 2, (float) 2, (float) 2, (float) 0.2, 250, 6);
        player.spigot().playEffect(player.getLocation().add(0, 1, 0), Effect.STEP_SOUND, 174, 1, (float) 2, (float) 2, (float) 2, (float) 0.2, 250, 6);
        Sounds.FREEZE1.play(player);

        for (Entity nearbyEntity : event.getPlayer().getNearbyEntities(6, 6, 6)) {
            if(!(nearbyEntity instanceof LivingEntity)) continue;
            if(nearbyEntity instanceof ArmorStand) continue;
            if(nearbyEntity instanceof Villager) continue;

            Misc.applyPotionEffect((LivingEntity) nearbyEntity, PotionEffectType.SLOW, 40, 100, false, false);
            Misc.applyPotionEffect((LivingEntity) nearbyEntity, PotionEffectType.WEAKNESS, 40, 100, false, false);
            nearbyEntity.getWorld().playEffect(nearbyEntity.getLocation(), Effect.SNOW_SHOVEL, 5);
            nearbyEntity.getWorld().playEffect(nearbyEntity.getLocation().add(0, 1, 0), Effect.SNOWBALL_BREAK, 5);

            if(!blocks.containsKey(nearbyEntity.getLocation().getBlock().getLocation()) && nearbyEntity.getLocation().getBlock().getType() == Material.AIR) {
                blocks.put(nearbyEntity.getLocation().getBlock().getLocation(), nearbyEntity.getLocation().getBlock().getType());
                nearbyEntity.getLocation().getBlock().setType(Material.ICE);
            }
            if(!blocks.containsKey(nearbyEntity.getLocation().add(0, 1, 0).getBlock().getLocation()) && nearbyEntity.getLocation().add(0, 1, 0).getBlock().getType() == Material.AIR) {
                blocks.put(nearbyEntity.getLocation().add(0, 1, 0).getBlock().getLocation(), nearbyEntity.getLocation().add(0, 1, 0).getBlock().getType());
                nearbyEntity.getLocation().add(0, 1, 0).getBlock().setType(Material.ICE);
            }

            Location tp = nearbyEntity.getLocation().getBlock().getLocation().add(0.5, 0, 0.5);
            ((CraftEntity) nearbyEntity).getHandle().setLocation(tp.getX(), tp.getY(), tp.getZ(), tp.getPitch(), tp.getYaw());
            nearbyEntity.teleport(tp);

        }

        EditSession session = SchematicPaste.loadSchematicAir(new File("plugins/WorldEdit/schematics/frozen.schematic"), location);
        sessions.add(session);

        new BukkitRunnable() {
            @Override
            public void run() {
                session.undo(session);
                sessions.remove(session);
                Sounds.FREEZE2.play(player);

                for (Map.Entry<Location, Material> entry : blocks.entrySet()) {
                    entry.getKey().getBlock().setType(entry.getValue());
                }
            }
        }.runTaskLater(PitSim.INSTANCE, 40);


    }

    @Override
    public List<String> getDescription(int enchantLvl) {
        return new ALoreBuilder("&7Freeze all nearby enemies for 3s", "&d&o-" + getManaCost(enchantLvl) + " Mana").getLore();
    }

    public static int getManaCost(int enchantLvl) {
        return 30 * (4 - enchantLvl);
    }
}
