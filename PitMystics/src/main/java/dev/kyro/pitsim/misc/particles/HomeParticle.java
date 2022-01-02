package dev.kyro.pitsim.misc.particles;

import dev.kyro.pitsim.PitSim;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class HomeParticle {
	public Location start;
	public Player end;
	public double stepSize;

	private Location currentLoc;

	private BukkitRunnable callback;

	public HomeParticle(Location start, Player end, double stepSize, BukkitRunnable callback) {
		this.start = start;
		this.end = end;
		this.stepSize = stepSize;
		this.callback = callback;

		currentLoc = start.clone();
		draw();
	}

	public void draw() {
		new BukkitRunnable() {
			@Override
			public void run() {
				if(!end.isOnline() || currentLoc.getWorld() != end.getWorld()) {
					cancel();
					display(end.getLocation().add(0, 1, 0));
					return;
				}
				Location endLoc = end.getLocation().add(0, 1, 0);
				if(currentLoc.distance(endLoc) < stepSize) {
					cancel();
					display(end.getLocation().add(0, 1, 0));
					callback.run();
					return;
				}

				display(currentLoc);
				Vector incrementVector = endLoc.toVector().subtract(currentLoc.toVector()).normalize().multiply(stepSize);
				currentLoc.add(incrementVector);
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 1L);
	}

	public void display(Location location) {
		location.getWorld().playEffect(location, Effect.HAPPY_VILLAGER, 1);
	}
}
