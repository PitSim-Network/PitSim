package dev.kyro.pitsim.misc;

import com.sk89q.worldedit.EditSession;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.slayers.EndermanSlayer;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.io.File;


public class TempBlock {

    public Location location;
    public Block block;
    public Material material;
    public String filePath = "null";
    public int time;
    public BukkitTask task;

    public TempBlock(Location location, Material material, int time){

        this.location = location;
        this.material = material;
        this.time = time;
        this.block = location.getBlock();

        TempBlockHelper.addBlockSession(block, block.getType());

        createTask();

        new BukkitRunnable() {
            @Override
            public void run() {

                task.cancel();

                TempBlockHelper.removeBlockSession(block);

                deleteAction();

            }
        }.runTaskLater(PitSim.INSTANCE, time* 20L);
    }

    public TempBlock(String filePath, Location location, int time){

        this.filePath = filePath;
        this.location = location;
        this.time = time;

        EditSession session = SchematicPaste.loadSchematicAir(new File(filePath), location);
        TempBlockHelper.sessions.add(session);

        createTask();

        new BukkitRunnable() {
            @Override
            public void run() {

                task.cancel();

                session.undo(session);
                TempBlockHelper.sessions.remove(session);

                deleteAction();

            }
        }.runTaskLater(PitSim.INSTANCE, time* 20L);
    }

    public void deleteAction(){

    }

    public void aliveAction(){

    }

    private void createTask(){
        this.task = new BukkitRunnable() {
            @Override
            public void run() {
                aliveAction();
            }
        }.runTaskTimer(PitSim.INSTANCE, 0, 20);
    }

    private void createTask(int time){
        this.task = new BukkitRunnable() {
            @Override
            public void run() {
                aliveAction();
            }
        }.runTaskTimer(PitSim.INSTANCE, 0, time*20L);
    }

    public TempBlock(String filePath, Location location, int time, int aliveActionDelay){
        EditSession session = SchematicPaste.loadSchematicAir(new File(filePath), location);
        TempBlockHelper.sessions.add(session);

        createTask(aliveActionDelay);

        new BukkitRunnable() {
            @Override
            public void run() {

                task.cancel();

                session.undo(session);
                TempBlockHelper.sessions.remove(session);

                deleteAction();

            }
        }.runTaskLater(PitSim.INSTANCE, time* 20L);
    }

    public TempBlock(Location location, Material material, int time, int aliveActionDelay){

        this.location = location;
        this.material = material;
        this.time = time;
        this.block = location.getBlock();

        TempBlockHelper.addBlockSession(block, block.getType());

        createTask(aliveActionDelay);

        new BukkitRunnable() {
            @Override
            public void run() {

                task.cancel();

                TempBlockHelper.removeBlockSession(block);

                deleteAction();

            }
        }.runTaskLater(PitSim.INSTANCE, time* 20L);
    }
}
