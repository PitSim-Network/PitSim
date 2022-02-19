package dev.kyro.pitsim.tutorial.objects;

import dev.kyro.pitsim.tutorial.Task;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

public abstract class TutorialSequence {
	public Player player;
	public Tutorial tutorial;
	public Task task;


	public TutorialSequence(Player player, Tutorial tutorial, Task task) {
		this.player = player;
		this.tutorial = tutorial;
		this.task = task;
	}

	public abstract List<BukkitTask> getRunnables();

	public abstract void play();

}
