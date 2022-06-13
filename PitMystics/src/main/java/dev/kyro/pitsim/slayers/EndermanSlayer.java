package dev.kyro.pitsim.slayers;

import com.xxmicloxx.NoteBlockAPI.NoteBlockAPI;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PitBoss;
import dev.kyro.pitsim.enums.SubLevel;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.slayers.tainted.SimpleBoss;
import dev.kyro.pitsim.slayers.tainted.SimpleSkin;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collection;
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
                Block block3 = locations.get(0).add(0,2,0).getBlock();

                block1.setType(Material.STAINED_CLAY);
                block2.setType(new ItemStack(Material.STAINED_CLAY, 1, (short) 2).getType());
                block3.setType(Material.STAINED_CLAY);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        try {
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