package dev.kyro.pitsim.events;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PitMob;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import dev.kyro.pitsim.misc.ThrowableBlock;
import dev.kyro.pitsim.slayers.IronGolemBoss;
import net.citizensnpcs.api.CitizensAPI;
import net.minecraft.server.v1_8_R3.EntityFallingBlock;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class ThrowBlock implements Listener {

    public static List<ThrowableBlock> ThrowableBlockClassHandler = new ArrayList();

    public static void addThrowableBlock(ThrowableBlock block){
        ThrowableBlockClassHandler.add(block);
    }

    @EventHandler
    public void onFall(EntityChangeBlockEvent event){
        if(event.getEntity() instanceof FallingBlock){
            for(ThrowableBlock block : ThrowableBlockClassHandler){
                if(block.getBlock().equals(event.getEntity())){
                    event.setCancelled(true);

                    block.run(event);
                    event.getEntity().remove();
                    ThrowableBlockClassHandler.remove(block);
                }
            }

        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        int items = 0;

        if (Misc.isAirOrNull(event.getPlayer().getItemInHand()) || event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
            return;

        Material block = event.getPlayer().getItemInHand().getType();

        if(block.equals(Material.ANVIL)){
            event.getPlayer().setVelocity(event.getPlayer().getLocation().getDirection());

            AOutput.send(event.getPlayer(), "&c&lWOO! &7I'm a block now!!!");
            Sounds.WITHER_SHOOT.play(event.getPlayer());
            event.getPlayer().getInventory().removeItem(new ItemStack(Material.ANVIL));
        }else if(block.equals(Material.SOUL_SAND)){
            //Vector dirVector = event.getPlayer().getEyeLocation().getDirection().subtract(event.getPlayer().getLocation().toVector()).setY(0);
            //Vector pullVector = dirVector.clone().normalize().setY(0.2).multiply(0.5).add(dirVector.clone().multiply(0.03));

            event.getPlayer().setVelocity(event.getPlayer().getLocation().getDirection());

            AOutput.send(event.getPlayer(), "&c&lWOO! &7I'm a block now!!!");
            Sounds.WITHER_SHOOT.play(event.getPlayer());
            event.getPlayer().getInventory().removeItem(new ItemStack(Material.SOUL_SAND));
        }else if (block.equals(Material.TNT)){
            event.getPlayer().setVelocity(event.getPlayer().getLocation().getDirection());

            AOutput.send(event.getPlayer(), "&c&lWOO! &7I'm a block now!!!");
            Sounds.WITHER_SHOOT.play(event.getPlayer());
            event.getPlayer().getInventory().removeItem(new ItemStack(Material.TNT));
            //ThrowBlock.addThrowableBlock(new ThrowableBlock(event.getPlayer(), Material.TNT, pullVector.multiply((0.5 * 0.2) + 1.15)));
        }

    }

}
