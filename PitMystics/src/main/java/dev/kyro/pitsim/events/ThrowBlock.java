package dev.kyro.pitsim.events;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.misc.ThrowableBlock;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.scheduler.BukkitRunnable;

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
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            block.run(event);
                            event.getEntity().remove();
                            ThrowableBlockClassHandler.remove(block);
                        }
                    }.runTaskLater(PitSim.INSTANCE, 30);
                }
            }

        }
    }

}
