package dev.kyro.pitsim.tutorial.objects;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

public abstract class TutorialSequence {
	public Player player;
	public Tutorial tutorial;


	public TutorialSequence(Player player, Tutorial tutorial) {
		this.player = player;
		this.tutorial = tutorial;
		
	}

	public abstract List<BukkitTask> getRunnables();
	public abstract void play();

}
