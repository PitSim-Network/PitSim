package dev.kyro.pitsim.tutorial.sequences;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.tutorial.MessageManager;
import dev.kyro.pitsim.tutorial.Task;
import dev.kyro.pitsim.tutorial.TutorialMessage;
import dev.kyro.pitsim.tutorial.inventories.ApplyEnchantPanel;
import dev.kyro.pitsim.tutorial.objects.Tutorial;
import dev.kyro.pitsim.tutorial.objects.TutorialSequence;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class ViewEnchantTiersSequence extends TutorialSequence {
	public Player player;
	public Tutorial tutorial;
	public int waitTime = 0;
	public List<BukkitTask> runnableList = new ArrayList<>();

	public ViewEnchantTiersSequence(Player player, Tutorial tutorial) {
		super(player, tutorial, Task.VIEW_ENCHANT_TIERS);
		this.player = player;
		this.tutorial = tutorial;
	}

	@Override
	public List<BukkitTask> getRunnables() {
		return runnableList;
	}

	@Override
	public void play() {
		openGUI();
		sendMessage(TutorialMessage.TIER1);
		wait(5);
		sendMessage(TutorialMessage.TIER2);
		wait(5);
		sendMessage(TutorialMessage.TIER3);
		wait(5);
		sendMessage(TutorialMessage.TIER4);
		wait(5);
		completeTask(Task.VIEW_ENCHANT_TIERS);
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
				player.openInventory(ApplyEnchantPanel.openEnchantsPanel(player).getInventory());
			}
		}.runTaskLater(PitSim.INSTANCE, (long) (20L * 0.5));
		runnableList.add(runnable);
	}


}
