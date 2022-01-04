package dev.kyro.pitsim.misc.particles;

import dev.kyro.pitsim.PitSim;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class LineParticle {
	public Player displayPlayer;
	public Location start;
	public Location end;
	public double stepSize;

	private int steps;
	private int currentStep = 0;
	private Location currentLoc;
	private Vector incrementVector;

	public LineParticle(Player displayPlayer, Location start, Location end, double stepSize) {
		this.displayPlayer = displayPlayer;
		this.start = start;
		this.end = end;
		this.stepSize = stepSize;

		double distance = start.distance(end);
		steps = (int) (distance / stepSize);
		currentLoc = start.clone();
		incrementVector = end.toVector().subtract(start.toVector()).normalize().multiply(stepSize);
		draw();
	}

	public void draw() {
		new BukkitRunnable() {
			@Override
			public void run() {
				if(currentStep > steps) {
					cancel();
					display(end);
					return;
				}

				display(currentLoc);
				currentLoc.add(incrementVector);

				currentStep++;
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 1L);
	}

	public void display(Location location) {
		displayPlayer.playEffect(EntityEffect.VILLAGER_HAPPY);
	}
}
