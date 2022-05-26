package dev.kyro.pitsim.misc;

import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.util.Vector;

public class ThrowableBlock implements Listener {

    FallingBlock block;
    Material material;
    Entity owner;

    public ThrowableBlock(Entity owner, Material material){
        this.owner = owner;
        this.material = material;

        this.block = owner.getWorld().spawnFallingBlock(owner.getLocation().add(0,1,0), this.material, (byte) 0);
        this.block.setDropItem(false);
        this.block.setHurtEntities(false);
        this.block.setVelocity(owner.getLocation().getDirection().multiply(3));
    }

    public ThrowableBlock(Entity owner, Material material, Vector vector){
        this.owner = owner;
        this.material = material;

        this.block = owner.getWorld().spawnFallingBlock(owner.getLocation().add(0,1,0), this.material, (byte) 0);
        this.block.setDropItem(false);
        this.block.setHurtEntities(false);
        this.block.setVelocity(vector);
    }

    public FallingBlock getBlock(){
        return this.block;
    }

    public void run(EntityChangeBlockEvent event){
        event.getEntity().getWorld().playEffect(event.getEntity().getLocation(), Effect.EXPLOSION_LARGE, 1);

        for (Entity player : event.getEntity().getNearbyEntities(5, 5, 5)) {
            if(!(player instanceof Player)) continue;
            PitPlayer.getPitPlayer((Player) player).damage(5.0, (LivingEntity) this.owner);
        }
    }

}
