package dev.kyro.pitsim.tutorial.sequences;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.tutorial.MessageManager;
import dev.kyro.pitsim.tutorial.Task;
import dev.kyro.pitsim.tutorial.TutorialMessage;
import dev.kyro.pitsim.tutorial.objects.Tutorial;
import dev.kyro.pitsim.tutorial.objects.TutorialSequence;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class SpawnNonSequence extends TutorialSequence {
	public Player player;
	public Tutorial tutorial;
	public int waitTime = 0;
	public List<BukkitTask> runnableList = new ArrayList<>();

	public SpawnNonSequence(Player player, Tutorial tutorial) {
		super(player, tutorial, Task.VIEW_NON);
		this.player = player;
		this.tutorial = tutorial;
		//test
	}

	@Override
	public List<BukkitTask> getRunnables() {
		return runnableList;
	}

	@Override
	public void play() {
		sendMessage(TutorialMessage.VIEWNON1);
		wait(5);
		spawnNons();
		sendMessage(TutorialMessage.VIEWNON2);
		wait(5);
		completeTask(Task.VIEW_NON);
	}

	public void wait(int seconds) {
		waitTime = waitTime + seconds;
	}

	public void sendMessage(TutorialMessage message) {
		BukkitTask runnable = new BukkitRunnable() {
			@Override
			public void run() {
				MessageManager.sendTutorialMessage(player, message);
			}
		}.runTaskLater(PitSim.INSTANCE, 20L * waitTime);
		runnableList.add(runnable);
	}

	public void completeTask(Task task) {
		BukkitTask runnable = new BukkitRunnable() {
			@Override
			public void run() {
				tutorial.onTaskComplete(task);
			}
		}.runTaskLater(PitSim.INSTANCE, 20L * waitTime);
		runnableList.add(runnable);
	}

	public void spawnNons() {
		BukkitTask runnable = new BukkitRunnable() {
			@Override
			public void run() {
				Bukkit.getWorld("tutorial").getBlockAt(tutorial.areaLocation).setType(Material.AIR);
				tutorial.mysticWellHolo.delete();
				NPC non  = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "Dummy");
				tutorial.nons.add(non);
				non.spawn(tutorial.areaLocation);
				non.setProtected(false);
			}
		}.runTaskLater(PitSim.INSTANCE, 20L * waitTime);
		runnableList.add(runnable);
	}


}
