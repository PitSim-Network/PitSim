package dev.kyro.pitsim.slayers;

import com.xxmicloxx.NoteBlockAPI.NoteBlockAPI;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.objects.PitBoss;
import dev.kyro.pitsim.enums.SubLevel;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.slayers.tainted.SimpleBoss;
import dev.kyro.pitsim.slayers.tainted.SimpleSkin;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEnderCrystal;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class EndermanSlayer extends PitBoss {
    public NPC npc;
    public Player entity;
    public Player target;
    public String name = "&c&lEnderman";
    public SubLevel subLevel = SubLevel.ENDERMAN_CAVE;
    public SimpleBoss boss;

    public EndermanSlayer(Player target) {
        super(target, SubLevel.ENDERMAN_CAVE);
        npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, name);

        this.boss = new SimpleBoss(npc, target, subLevel, 5, SimpleSkin.ENDERMAN, this){

            @Override
            protected void attackHigh(){

            }

            @Override
            protected void attackMedium(){
                List<Location> locations = new ArrayList<>();

                locations.add(new Location(target.getWorld(), 378.5, 20, -221.5));
                locations.add(new Location(target.getWorld(), 388.5, 19, -211.5));

                locations.add(new Location(target.getWorld(), 398.5, 18, -219.5));

                locations.add(new Location(target.getWorld(), 396.5, 18, -230.5));

                locations.add(new Location(target.getWorld(), 397.5, 18, -232.5));


                Collections.shuffle(locations);

                Block block1 = locations.get(0).getBlock();
                Block block2 = locations.get(0).add(0,1,0).getBlock();

                Location location = locations.get(0).add(0,1,0);

                Block block3 = location.getBlock();

                block1.setType(Material.QUARTZ_BLOCK);
                block2.setType(Material.SEA_LANTERN);
                block3.setType(Material.QUARTZ_BLOCK);

                BukkitTask runnable = new BukkitRunnable() {
                    @Override
                    public void run() {
                        try {
                            Vector diff = target.getLocation().add(0.5, 1, 0.5).subtract(location.add(0, 1, 0)).toVector();
                            Location base = location.add(0, 1, 0)/* the origin, where you are moving away from */;
                            double add = diff.length(); //example amount
                            diff.divide(new Vector(add, add, add));

                            for (int i = 0; i < add; i++) {
                                base.add(diff);
                                EndermanSlayer.this.target.getWorld().spigot().playEffect(base, Effect.ENDER_SIGNAL, 0, 0, (float) 0, (float) 0/255, (float) 0/255, 1, 0, 64);
                                EndermanSlayer.this.target.getWorld().spigot().playEffect(base, Effect.ENDER_SIGNAL, 0, 0, (float) 0, (float) 0/255, (float) 0/255, 1, 0, 64);
                                EndermanSlayer.this.target.getWorld().spigot().playEffect(base, Effect.ENDER_SIGNAL, 0, 0, (float) 0, (float) 0/255, (float) 0/255, 1, 0, 64);
                                EndermanSlayer.this.target.getWorld().spigot().playEffect(base, Effect.ENDER_SIGNAL, 0, 0, (float) 0, (float) 0/255, (float) 0/255, 1, 0, 64);
                                EndermanSlayer.this.target.getWorld().spigot().playEffect(base, Effect.ENDER_SIGNAL, 0, 0, (float) 0, (float) 0/255, (float) 0/255, 1, 0, 64);
                                EndermanSlayer.this.target.getWorld().spigot().playEffect(base, Effect.ENDER_SIGNAL, 0, 0, (float) 0, (float) 0/255, (float) 0/255, 1, 0, 64);
                                EndermanSlayer.this.target.getWorld().spigot().playEffect(base, Effect.ENDER_SIGNAL, 0, 0, (float) 0, (float) 0/255, (float) 0/255, 1, 0, 64);
                                EndermanSlayer.this.target.getWorld().spigot().playEffect(base, Effect.ENDER_SIGNAL, 0, 0, (float) 0, (float) 0/255, (float) 0/255, 1, 0, 64);
                                EndermanSlayer.this.target.getWorld().spigot().playEffect(base, Effect.ENDER_SIGNAL, 0, 0, (float) 0, (float) 0/255, (float) 0/255, 1, 0, 64);
                                EndermanSlayer.this.target.getWorld().spigot().playEffect(base, Effect.ENDER_SIGNAL, 0, 0, (float) 0, (float) 0/255, (float) 0/255, 1, 0, 64);
                            }

                            //EntityDamageByEntityEvent damageEvent = new EntityDamageByEntityEvent(npc.getEntity(), EndermanSlayer.this.target, EntityDamageEvent.DamageCause.ENTITY_ATTACK, 4);
                            //Bukkit.getServer().getPluginManager().callEvent(damageEvent);
                            //if(!damageEvent.isCancelled()) EndermanSlayer.this.target.damage(4, target);
                        } catch (Exception ignored) {
                        }
                    }
                }.runTaskTimer(PitSim.INSTANCE, 0, 20);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        try {
                            runnable.cancel();
                            block1.setType(Material.AIR);
                            block2.setType(Material.AIR);
                            block3.setType(Material.AIR);
                        } catch (Exception ignored) { }
                    }
                }.runTaskLater(PitSim.INSTANCE, 200);

            }

            @Override
            protected void attackLow(){

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