package dev.kyro.pitsim.cosmetics.misc.kyrocosmetic;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.SpawnManager;
import dev.kyro.pitsim.cosmetics.CosmeticManager;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public abstract class AIParticle {
	public static List<AIParticle> particleList = new ArrayList<>();

	public Player owner;
	public LivingEntity target;
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

	public void pickTarget() {
		if(SpawnManager.isInSpawn(owner.getLocation())) {
			target = null;
			return;
		}
		target = Misc.getMobPlayerClosest(particleLocation, 15, owner);
	}

	public void display(Effect effect) {
		for(Player player : CosmeticManager.getDisplayPlayers(owner, particleLocation, 50))
			player.playEffect(particleLocation, effect, 1);
	}
}
