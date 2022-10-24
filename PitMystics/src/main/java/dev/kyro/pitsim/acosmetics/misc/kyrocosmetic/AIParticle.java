package dev.kyro.pitsim.acosmetics.misc.kyrocosmetic;

import de.myzelyam.api.vanish.VanishAPI;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.acosmetics.CosmeticManager;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public abstract class AIParticle {
	public static List<AIParticle> particleList = new ArrayList<>();

	public Player owner;
	public Player target;
	public Location particleLocation;

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				for(AIParticle particle : new ArrayList<>(particleList)) {
					if(!particle.owner.isOnline() || particle.owner.getWorld() != particle.particleLocation.getWorld()) {
						particle.remove();
						continue;
					}
					particle.tick();
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 1);
	}

	public AIParticle(Player owner, Player target, Location particleLocation) {
		this.owner = owner;
		this.target = target;
		this.particleLocation = particleLocation;

		particleList.add(this);
	}

	public abstract void tick();

	public void remove() {
		particleList.remove(this);
	}

	public Player pickTarget() {
		Player closestPlayer = null;
		double distance = Double.MAX_VALUE;
		for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			if(onlinePlayer == owner || onlinePlayer.getWorld() != owner.getWorld() || VanishAPI.isInvisible(onlinePlayer)) continue;
			double testDistance = onlinePlayer.getLocation().distance(owner.getLocation());
			if(testDistance > 15 || testDistance > distance) continue;
			closestPlayer = onlinePlayer;
			distance = testDistance;
		}
		target = closestPlayer;
		return target;
	}

	public void display(Effect effect) {
		for(Player player : CosmeticManager.getDisplayPlayers(owner, particleLocation)) player.playEffect(particleLocation, effect, 1);
	}
}
