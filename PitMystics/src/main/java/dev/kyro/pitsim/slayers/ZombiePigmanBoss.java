package dev.kyro.pitsim.slayers;

import com.xxmicloxx.NoteBlockAPI.NoteBlockAPI;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PitBoss;
import dev.kyro.pitsim.controllers.objects.PitMob;
import dev.kyro.pitsim.enums.SubLevel;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Sounds;
import dev.kyro.pitsim.mobs.PitStrongPigman;
import dev.kyro.pitsim.mobs.PitZombiePigman;
import dev.kyro.pitsim.slayers.tainted.SimpleBoss;
import dev.kyro.pitsim.slayers.tainted.SimpleSkin;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class ZombiePigmanBoss extends PitBoss {
    public NPC npc;
    public Player entity;
    public Player target;
    public String name = "&c&lPigman";
    public SubLevel subLevel = SubLevel.PIGMEN_CAVE;
    public SimpleBoss boss;

    public List<PitMob> pigmen = new ArrayList<>();

    public ZombiePigmanBoss(Player target) {
        super(target, SubLevel.PIGMEN_CAVE);
        npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, name);

        this.boss = new SimpleBoss(npc, target, subLevel, 5, SimpleSkin.PIGMAN, this){

            @Override
            protected void attackHigh(){

            }

            @Override
            protected void attackMedium(){

                for (PitMob pigman : pigmen) {
                    if(!pigman.entity.isDead()) return;
                }

                Sounds.REPEL.play(target.getLocation());

                for (Entity entity : target.getNearbyEntities(6, 6, 6)) {
                    Vector dirVector = entity.getLocation().toVector().subtract(target.getLocation().toVector()).normalize();
                    Vector pullVector = dirVector.clone().normalize().setY(0.5).multiply(2.5).add(dirVector.clone().multiply(0.03));
                    if(entity == npc.getEntity()) continue;
                    entity.setVelocity(pullVector);
                }

                pigmen.add(new PitStrongPigman(target.getLocation().clone().add(2, 0, 0)));
                pigmen.add(new PitStrongPigman(target.getLocation().clone().add(-2, 0, 0)));
                pigmen.add(new PitStrongPigman(target.getLocation().clone().add(0, 0, 2)));
                pigmen.add(new PitStrongPigman(target.getLocation().clone().add(0, 0, -2)));

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        for (PitMob pigman : pigmen) {
                            ((PigZombie) pigman.entity).setTarget(target);
                        }
                    }
                }.runTaskLater(PitSim.INSTANCE, 5);

                for (int i = 0; i < 4; i++) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            Sounds.ANVIL_LAND.play(target);
                        }
                    }.runTaskLater(PitSim.INSTANCE, i * 5);
                }


            }

            @Override
            protected void attackLow() {
                Vector dirVector = target.getLocation().toVector().subtract(npc.getEntity().getLocation().toVector()).setY(0);
                Vector pullVector = dirVector.clone().normalize().setY(0.5).multiply(2.5).add(dirVector.clone().multiply(0.03));
                npc.getEntity().setVelocity(pullVector.multiply(1.25));

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Vector dirVector = target.getLocation().toVector().subtract(npc.getEntity().getLocation().toVector()).normalize();
                        Vector pullVector = dirVector.clone().normalize().setY(0.5).multiply(2.5).add(dirVector.clone().multiply(0.03));
                        target.setVelocity(pullVector);

                        Sounds.LUCKY_SHOT.play(target);
                        target.damage(15, npc.getEntity());
                    }
                }.runTaskLater(PitSim.INSTANCE, 10);
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
        for (PitMob pigman : pigmen) {
            pigman.entity.damage(1000);
        }
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