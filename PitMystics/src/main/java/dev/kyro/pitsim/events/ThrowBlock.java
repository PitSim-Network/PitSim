package dev.kyro.pitsim.events;

import dev.kyro.pitsim.misc.ThrowableBlock;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

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
                    block.run(event);
                    event.setCancelled(true);
                    event.getEntity().remove();
                    ThrowableBlockClassHandler.remove(block);
                }
            }

        }
    }

}
