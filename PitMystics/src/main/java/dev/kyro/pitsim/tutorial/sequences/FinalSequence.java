package dev.kyro.pitsim.tutorial.sequences;

import dev.kyro.arcticapi.data.APlayer;
import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.MapManager;
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

public class FinalSequence extends TutorialSequence {
	public Player player;
	public Tutorial tutorial;
	public int waitTime = 0;
	public List<BukkitTask> runnableList = new ArrayList<>();

	public FinalSequence(Player player, Tutorial tutorial) {
		super(player, tutorial, Task.FINISH_TUTORIAL);
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
		wait(2);
		sendMessage(TutorialMessage.FINAL1);
		wait(5);
		sendMessage(TutorialMessage.FINAL2);
		wait(5);
		sendMessage(TutorialMessage.FINAL3);
		wait(5);
		teleport();
		wait(1);
		complete();
		completeTask(Task.FINISH_TUTORIAL);
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

	public void teleport() {
		BukkitTask runnable = new BukkitRunnable() {
			@Override
			public void run() {
				player.teleport(MapManager.currentMap.firstLobby.getSpawnLocation());
			}
		}.runTaskLater(PitSim.INSTANCE, 20L * waitTime);
		runnableList.add(runnable);
	}

	public void complete() {
		BukkitTask runnable = new BukkitRunnable() {
			@Override
			public void run() {
				tutorial.cleanUp();
				APlayer aPlayer = APlayerData.getPlayerData(player);
				aPlayer.playerData.set("tutorial", true);
				aPlayer.save();
			}
		}.runTaskLater(PitSim.INSTANCE, 20L * waitTime);
		runnableList.add(runnable);
	}

}
