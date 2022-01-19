package dev.kyro.pitsim.tutorial.sequences;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.tutorial.MessageManager;
import dev.kyro.pitsim.tutorial.Task;
import dev.kyro.pitsim.tutorial.TutorialMessage;
import dev.kyro.pitsim.tutorial.objects.Tutorial;
import dev.kyro.pitsim.tutorial.objects.TutorialSequence;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class InitialSequence extends TutorialSequence {
	public Player player;
	public Tutorial tutorial;
	public double waitTime = 0.1;
	public List<BukkitTask> runnableList = new ArrayList<>();

	public InitialSequence(Player player, Tutorial tutorial) {
		super(player, tutorial, Task.VIEW_MAP);
		this.player = player;
		this.tutorial = tutorial;
	}

	@Override
	public List<BukkitTask> getRunnables() {
		return runnableList;
	}

	@Override
	public void play() {
		wait(5);
		sendMessage(TutorialMessage.WELCOME1);
		wait(5);
		sendMessage(TutorialMessage.WELCOME2);
		wait(5);
		sendMessage(TutorialMessage.WELCOME3);
		wait(5);
		sendMessage(TutorialMessage.WELCOME4);
		wait(5);
		sendMessage(TutorialMessage.WELCOME5);
		wait(5);
		teleportPlayer();
		completeTask(Task.VIEW_MAP);
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
		}.runTaskLater(PitSim.INSTANCE, (long) (20 * waitTime));
		runnableList.add(runnable);
	}

	public void completeTask(Task task) {
		BukkitTask runnable = new BukkitRunnable() {
			@Override
			public void run() {
				tutorial.onTaskComplete(task);
			}
		}.runTaskLater(PitSim.INSTANCE, (long) (20 * waitTime));
		runnableList.add(runnable);
	}

	public void teleportPlayer() {
		BukkitTask runnable = new BukkitRunnable() {
			@Override
			public void run() {
				Location location = new Location(Bukkit.getWorld("tutorial"), tutorial.positionCoords.x, 93, tutorial.positionCoords.y);
				player.teleport(location);
			}
		}.runTaskLater(PitSim.INSTANCE, (long) (20 * waitTime));
		runnableList.add(runnable);
	}

}
