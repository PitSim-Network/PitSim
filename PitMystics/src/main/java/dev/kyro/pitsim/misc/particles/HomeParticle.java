package dev.kyro.pitsim.misc.particles;

import dev.kyro.pitsim.PitSim;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class HomeParticle {
	public Player displayPlayer;
	public Location start;
	public Player end;
	public double stepSize;

	private Location currentLoc;

	private BukkitRunnable callback;

	public HomeParticle(Player displayPlayer, Location start, Player end, double stepSize, BukkitRunnable callback) {
		this.displayPlayer = displayPlayer;
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
				if(currentLoc.getWorld() != end.getWorld()) {
					cancel();
					return;
				}
				Location endLoc = end.getLocation().add(0, 1, 0);
				if(currentLoc.distance(endLoc) < stepSize) {
					cancel();
//					display(end.getLocation().add(0, 1, 0));
					for(int i = 0; i < 5; i++) displayPlayer.playEffect(endLoc, Effect.LAVA_POP, 1);
					displayPlayer.playSound(endLoc, Sound.LAVA_POP, 1, (float) (Math.random() * 0.5 + 0.9));
					callback.runTask(PitSim.INSTANCE);
					return;
				}

				display(currentLoc);
				Vector incrementVector = endLoc.toVector().subtract(currentLoc.toVector()).normalize().multiply(stepSize);
				currentLoc.add(incrementVector);
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 1L);
	}

	public void display(Location location) {
		displayPlayer.playEffect(location, Effect.HAPPY_VILLAGER, 1);
	}
}
