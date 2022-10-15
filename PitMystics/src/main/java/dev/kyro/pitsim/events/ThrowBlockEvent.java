package dev.kyro.pitsim.events;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import dev.kyro.pitsim.misc.ThrowableBlock;
import org.bukkit.Material;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ThrowBlockEvent implements Listener {

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
