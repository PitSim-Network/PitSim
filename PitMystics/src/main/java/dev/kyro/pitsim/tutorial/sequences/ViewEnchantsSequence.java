package dev.kyro.pitsim.tutorial.sequences;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.tutorial.MessageManager;
import dev.kyro.pitsim.tutorial.Task;
import dev.kyro.pitsim.tutorial.TutorialMessage;
import dev.kyro.pitsim.tutorial.inventories.EnchantingPanel;
import dev.kyro.pitsim.tutorial.objects.Tutorial;
import dev.kyro.pitsim.tutorial.objects.TutorialSequence;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class ViewEnchantsSequence extends TutorialSequence {
	public Player player;
	public Tutorial tutorial;
	public int waitTime = 0;
	public List<BukkitTask> runnableList = new ArrayList<>();

	public ViewEnchantsSequence(Player player, Tutorial tutorial) {
		super(player, tutorial, Task.VIEW_ENCHANTS);
		this.player = player;
		this.tutorial = tutorial;
	}

	@Override
	public List<BukkitTask> getRunnables() {
		return runnableList;
	}

	@Override
	public void play() {
		wait(2);
		openGUI();
		wait(5);
		sendMessage(TutorialMessage.DARK_BLUE);
		sendMessage(TutorialMessage.DARK_GREEN);
		completeTask(Task.VIEW_ENCHANTS);
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

	public void openGUI() {
		BukkitTask runnable = new BukkitRunnable() {
			@Override
			public void run() {
				player.openInventory(EnchantingPanel.openEnchantsPanel(player).getInventory());
			}
		}.runTaskLater(PitSim.INSTANCE, 20L * waitTime);
		runnableList.add(runnable);
	}


}
