package dev.kyro.pitsim.slayers;

import com.xxmicloxx.NoteBlockAPI.NoteBlockAPI;
import dev.kyro.pitsim.controllers.objects.PitBoss;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.SubLevel;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.ThrowBlock;
import dev.kyro.pitsim.misc.Sounds;
import dev.kyro.pitsim.misc.ThrowableBlock;
import dev.kyro.pitsim.slayers.tainted.SimpleBoss;
import dev.kyro.pitsim.slayers.tainted.SimpleSkin;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class IronGolemBoss extends PitBoss {
    public NPC npc;
    public Player entity;
    public Player target;
    public String name = "&c&lIron Golem";
    public SubLevel subLevel = SubLevel.GOLEM_CAVE;
    public SimpleBoss boss;

    public IronGolemBoss(Player target) {
        super(target, SubLevel.GOLEM_CAVE);
        npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, name);

        this.boss = new SimpleBoss(npc, target, subLevel, 5, SimpleSkin.IRON_GOLEM, this){

            @Override
            protected void attackHigh(){

            }

            @Override
            protected void attackMedium(){
                if(npc.getEntity().getLocation().distance(target.getLocation()) <= 7) {
                    Vector diff = target.getLocation().add(0.5, 1, 0.5).subtract(npc.getEntity().getLocation().clone().add(0, 1, 0)).toVector();
                    Location base = npc.getEntity().getLocation().clone().add(0, 1, 0)/* the origin, where you are moving away from */;
                    double add = diff.length(); //example amount
                    diff.divide(new Vector(add, add, add));

                    for (int i = 0; i < add; i++) {
                        base.add(diff);
                        Sounds.RGM.play(target);
                        target.getWorld().spigot().playEffect(base, Effect.MAGIC_CRIT, 0, 0, (float) 0, (float) 0 / 255, (float) 0 / 255, 1, 0, 64);
                        target.getWorld().spigot().playEffect(base, Effect.MAGIC_CRIT, 0, 0, (float) 0, (float) 0 / 255, (float) 0 / 255, 1, 0, 64);
                        target.getWorld().spigot().playEffect(base, Effect.MAGIC_CRIT, 0, 0, (float) 0, (float) 0 / 255, (float) 0 / 255, 1, 0, 64);
                        target.getWorld().spigot().playEffect(base, Effect.MAGIC_CRIT, 0, 0, (float) 0, (float) 0 / 255, (float) 0 / 255, 1, 0, 64);
                        target.getWorld().spigot().playEffect(base, Effect.MAGIC_CRIT, 0, 0, (float) 0, (float) 0 / 255, (float) 0 / 255, 1, 0, 64);
                        target.getWorld().spigot().playEffect(base, Effect.MAGIC_CRIT, 0, 0, (float) 0, (float) 0 / 255, (float) 0 / 255, 1, 0, 64);
                        target.getWorld().spigot().playEffect(base, Effect.MAGIC_CRIT, 0, 0, (float) 0, (float) 0 / 255, (float) 0 / 255, 1, 0, 64);
                        target.getWorld().spigot().playEffect(base, Effect.MAGIC_CRIT, 0, 0, (float) 0, (float) 0 / 255, (float) 0 / 255, 1, 0, 64);
                        target.getWorld().spigot().playEffect(base, Effect.MAGIC_CRIT, 0, 0, (float) 0, (float) 0 / 255, (float) 0 / 255, 1, 0, 64);
                        target.getWorld().spigot().playEffect(base, Effect.MAGIC_CRIT, 0, 0, (float) 0, (float) 0 / 255, (float) 0 / 255, 1, 0, 64);
                    }

                    EntityDamageByEntityEvent damageEvent = new EntityDamageByEntityEvent(npc.getEntity(), target, EntityDamageEvent.DamageCause.ENTITY_ATTACK, 4);
                    Bukkit.getServer().getPluginManager().callEvent(damageEvent);
                    if (!damageEvent.isCancelled()) target.damage(4, target);
                }
            }

            @Override
            protected void attackLow(){
                if(npc.getEntity() != null)
                    ThrowBlock.addThrowableBlock(new ThrowableBlock(npc.getEntity(), Material.ANVIL){
                        @Override
                        public void run(EntityChangeBlockEvent event){
                            event.getEntity().getWorld().playEffect(event.getEntity().getLocation(), Effect.MAGIC_CRIT, 1);

                            for (Entity player : event.getEntity().getNearbyEntities(5, 5, 5)) {
                                
                                if(!(player instanceof Player)) continue;
                                Sounds.ANVIL_LAND.play((LivingEntity) player);
                                PitPlayer.getPitPlayer((Player) player).damage(10.0, (LivingEntity) this.owner);
                                player.setVelocity(new Vector(0, 3, 0));
                            }
                        }
                    });
            }

            @Override
            protected void defend() {

            }

        };
        this.entity = (Player) npc.getEntity();
        this.target = target;

        boss.run();


    }

    @Override
    public void onAttack(AttackEvent.Apply event) throws Exception {
        boss.attackAbility(event);
    }

    @Override
    public void onDefend() {
        boss.defendAbility();
    }

    @Override
    public void onDeath() {
        boss.hideActiveBossBar();
        NoteBlockAPI.stopPlaying(target);
    }

    @Override
    public Player getEntity() {
        return (Player) npc.getEntity();
    }

    @Override
    public void setNPC(NPC npc) {
        this.npc = npc;
    }
}