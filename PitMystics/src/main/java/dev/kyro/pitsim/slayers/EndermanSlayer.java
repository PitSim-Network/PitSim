package dev.kyro.pitsim.slayers;

import com.xxmicloxx.NoteBlockAPI.NoteBlockAPI;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.brewing.PotionManager;
import dev.kyro.pitsim.controllers.objects.PitBoss;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.SubLevel;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Sounds;
import dev.kyro.pitsim.slayers.tainted.SimpleBoss;
import dev.kyro.pitsim.slayers.tainted.SimpleSkin;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
                EndermanSlayer.this.target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 50, 50, true, true));
                EndermanSlayer.this.target.getWorld().spigot().playEffect(EndermanSlayer.this.target.getLocation(), Effect.ENDER_SIGNAL, 0, 0, (float) 0, (float) 0/255, (float) 0/255, 1, 0, 64);
                EndermanSlayer.this.target.getWorld().spigot().playEffect(EndermanSlayer.this.target.getLocation(), Effect.ENDER_SIGNAL, 0, 0, (float) 0, (float) 0/255, (float) 0/255, 1, 0, 64);
                EndermanSlayer.this.target.getWorld().spigot().playEffect(EndermanSlayer.this.target.getLocation(), Effect.ENDER_SIGNAL, 0, 0, (float) 0, (float) 0/255, (float) 0/255, 1, 0, 64);
                EndermanSlayer.this.target.getWorld().spigot().playEffect(EndermanSlayer.this.target.getLocation(), Effect.ENDER_SIGNAL, 0, 0, (float) 0, (float) 0/255, (float) 0/255, 1, 0, 64);
                EndermanSlayer.this.target.getWorld().spigot().playEffect(EndermanSlayer.this.target.getLocation(), Effect.ENDER_SIGNAL, 0, 0, (float) 0, (float) 0/255, (float) 0/255, 1, 0, 64);
                EndermanSlayer.this.target.getWorld().spigot().playEffect(EndermanSlayer.this.target.getLocation(), Effect.ENDER_SIGNAL, 0, 0, (float) 0, (float) 0/255, (float) 0/255, 1, 0, 64);

                target.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5This is my final message... &c&lGOODBYE!"));

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        target.getWorld().playEffect(target.getLocation(), Effect.FIREWORKS_SPARK, 1);
                        Sounds.PRESTIGE.play(EndermanSlayer.this.target);
                        if(npc.getEntity() != null){
                            for (Entity player : npc.getEntity().getNearbyEntities(5, 5, 5)) {
                                if(player != target) continue;
                                PitPlayer.getPitPlayer((Player) player).damage(EndermanSlayer.this.target.getHealth(), (LivingEntity) npc.getEntity());
                            }
                        }
                        EndermanSlayer.this.target.removePotionEffect(PotionEffectType.SLOW);
                    }
                }.runTaskLater(PitSim.INSTANCE, 30);

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
                            if(location.distance(EndermanSlayer.this.target.getLocation()) <= 7){
                                Vector diff = target.getLocation().add(0.5, 1, 0.5).subtract(location.clone().add(0, 1, 0)).toVector();
                                Location base = location.clone().add(0, 1, 0)/* the origin, where you are moving away from */;
                                double add = diff.length(); //example amount
                                diff.divide(new Vector(add, add, add));

                                for (int i = 0; i < add; i++) {
                                    base.add(diff);
                                    Sounds.RGM.play(EndermanSlayer.this.target);
                                    EndermanSlayer.this.target.getWorld().spigot().playEffect(base, Effect.FLYING_GLYPH, 0, 0, (float) 0, (float) 0/255, (float) 0/255, 1, 0, 64);
                                    EndermanSlayer.this.target.getWorld().spigot().playEffect(base, Effect.FLYING_GLYPH, 0, 0, (float) 0, (float) 0/255, (float) 0/255, 1, 0, 64);
                                    EndermanSlayer.this.target.getWorld().spigot().playEffect(base, Effect.FLYING_GLYPH, 0, 0, (float) 0, (float) 0/255, (float) 0/255, 1, 0, 64);
                                    EndermanSlayer.this.target.getWorld().spigot().playEffect(base, Effect.FLYING_GLYPH, 0, 0, (float) 0, (float) 0/255, (float) 0/255, 1, 0, 64);
                                    EndermanSlayer.this.target.getWorld().spigot().playEffect(base, Effect.FLYING_GLYPH, 0, 0, (float) 0, (float) 0/255, (float) 0/255, 1, 0, 64);
                                    EndermanSlayer.this.target.getWorld().spigot().playEffect(base, Effect.FLYING_GLYPH, 0, 0, (float) 0, (float) 0/255, (float) 0/255, 1, 0, 64);
                                    EndermanSlayer.this.target.getWorld().spigot().playEffect(base, Effect.FLYING_GLYPH, 0, 0, (float) 0, (float) 0/255, (float) 0/255, 1, 0, 64);
                                    EndermanSlayer.this.target.getWorld().spigot().playEffect(base, Effect.FLYING_GLYPH, 0, 0, (float) 0, (float) 0/255, (float) 0/255, 1, 0, 64);
                                    EndermanSlayer.this.target.getWorld().spigot().playEffect(base, Effect.FLYING_GLYPH, 0, 0, (float) 0, (float) 0/255, (float) 0/255, 1, 0, 64);
                                    EndermanSlayer.this.target.getWorld().spigot().playEffect(base, Effect.FLYING_GLYPH, 0, 0, (float) 0, (float) 0/255, (float) 0/255, 1, 0, 64);
                                }

                                if(EndermanSlayer.this.target.hasPotionEffect(PotionEffectType.SLOW)){
                                    EndermanSlayer.this.target.removePotionEffect(PotionEffectType.SLOW);
                                    EndermanSlayer.this.target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 9, true, true));
                                }else EndermanSlayer.this.target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 9, true, true));

                                if(EndermanSlayer.this.target.hasPotionEffect(PotionEffectType.WEAKNESS)){
                                    EndermanSlayer.this.target.removePotionEffect(PotionEffectType.WEAKNESS);
                                    EndermanSlayer.this.target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100, 3, true, true));
                                }else EndermanSlayer.this.target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100, 3, true, true));

                                //EntityDamageByEntityEvent damageEvent = new EntityDamageByEntityEvent(npc.getEntity(), EndermanSlayer.this.target, EntityDamageEvent.DamageCause.ENTITY_ATTACK, 4);
                                //Bukkit.getServer().getPluginManager().callEvent(damageEvent);
                                //if(!damageEvent.isCancelled()) EndermanSlayer.this.target.damage(4, target);
                            }

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