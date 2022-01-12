package dev.kyro.pitsim.tutorial.sequences;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.tutorial.MessageManager;
import dev.kyro.pitsim.tutorial.Task;
import dev.kyro.pitsim.tutorial.TutorialMessage;
import dev.kyro.pitsim.tutorial.objects.Tutorial;
import dev.kyro.pitsim.tutorial.objects.TutorialSequence;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class VampireSequence extends TutorialSequence {
	public Player player;
	public Tutorial tutorial;
	public double waitTime = 0.1;
	public List<BukkitTask> runnableList = new ArrayList<>();

	public VampireSequence(Player player, Tutorial tutorial) {
		super(player, tutorial, Task.EQUIP_VAMPIRE);
		this.player = player;
		this.tutorial = tutorial;
		player.closeInventory();
	}

	@Override
	public List<BukkitTask> getRunnables() {
		return runnableList;
	}

	@Override
	public void play() {
		spawnVillager();
		wait(3);
		sendMessage(TutorialMessage.DARK_BLUE);
		wait(2);
		sendMessage(TutorialMessage.DARK_GREEN);

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
		}.runTaskLater(PitSim.INSTANCE, (long) (20L * waitTime));
		runnableList.add(runnable);
	}

	public void completeTask(Task task) {
		BukkitTask runnable = new BukkitRunnable() {
			@Override
			public void run() {
				tutorial.onTaskComplete(task);
			}
		}.runTaskLater(PitSim.INSTANCE, (long) (20L * waitTime));
		runnableList.add(runnable);
	}

	public void spawnVillager() {
		BukkitTask runnable = new BukkitRunnable() {
			@Override
			public void run() {
				tutorial.spawnUpgradesNPC();
			}
		}.runTaskLater(PitSim.INSTANCE, (long) (20L * waitTime));
		runnableList.add(runnable);
	}


}
