package dev.kyro.pitsim.adarkzone.abilities;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.PitBossAbility;
import dev.kyro.pitsim.cosmetics.ParticleOffset;
import dev.kyro.pitsim.cosmetics.particles.CloudParticle;
import dev.kyro.pitsim.cosmetics.particles.ParticleColor;
import dev.kyro.pitsim.cosmetics.particles.RedstoneParticle;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Random;

public class ReincarnationAbility extends PitBossAbility {
	public boolean hasActivated;
	public boolean isReincarnating;
	public int randomThreshold = 50;
	public int rays = 25;

	@Override
	public void onRoutineExecute() {
		activate();
	}

	@EventHandler
	public void onAttack(AttackEvent.Pre attackEvent) {
		if(!isAssignedBoss(attackEvent.getDefender()) || !isReincarnating) return;
		attackEvent.setCancelled(true);
	}

	@EventHandler
	public void onAttack(AttackEvent.Post attackEvent) {
		if(!isAssignedBoss(attackEvent.getDefender())) return;
		if(attackEvent.getDefender().getHealth() / attackEvent.getDefender().getMaxHealth() > 0.2) return;
		activate();
	}

	public void activate() {
		if(hasActivated) return;
		hasActivated = true;
		isReincarnating = true;
		Sounds.REINCARNATION.play(getPitBoss().getBoss().getLocation(), 40);

		Location top = getPitBoss().getBoss().getLocation().add(0, 4, 0);
		Vector velocity = top.clone().subtract(getPitBoss().getBoss().getLocation()).toVector().multiply(0.1);
		getPitBoss().getBoss().setVelocity(velocity);

		getPitBoss().getBoss().setAllowFlight(true);
		getPitBoss().getBoss().setFlying(true);
		createRays(top);

		CloudParticle cloudParticle = new CloudParticle();
		List<Player> viewers = getViewers();

		new BukkitRunnable() {
			int ticks = 0;

			@Override
			public void run() {
				getPitBoss().getBoss().teleport(top);

				for(int i = 0; i < 25; i++) {
					for(Player viewer : viewers) {
						cloudParticle.display(viewer, top, new ParticleOffset(0, 0 ,0, 2, 0, 2));
					}
				}

				ticks++;
				if(ticks >= 60) cancel();
			}
		}.runTaskTimer(PitSim.INSTANCE, 20, 1);

		new BukkitRunnable() {
			@Override
			public void run() {
				getPitBoss().getBoss().setFlying(false);
				getPitBoss().getBoss().setAllowFlight(false);

				if(getPitBoss().getBoss().isDead()) return;
				getPitBoss().getBoss().setHealth(getPitBoss().getBoss().getMaxHealth());
				isReincarnating = false;
				getPitBoss().onHealthChange();
				Sounds.REINCARNATION_END.play(getPitBoss().getBoss().getLocation(), 40);
			}
		}.runTaskLater(PitSim.INSTANCE, 80);
	}

	public void createRays(Location center) {
		RedstoneParticle particle = new RedstoneParticle();

		Random random = new Random();

		for(int i = 0; i < rays; i++) {
			int randomX = random.nextInt(randomThreshold - (-1 * randomThreshold)) + (-1 * randomThreshold);
			int randomZ = random.nextInt(randomThreshold - (-1 * randomThreshold)) + (-1 * randomThreshold);
			int randomY = random.nextInt(40) + 3;

			Location endpoint = center.clone().add(randomX, randomY, randomZ);
			Vector stepVector = endpoint.clone().subtract(center).toVector().normalize().multiply(0.25);


			Location displayLocation = center.clone();
			getPitBoss().getBoss().teleport(center);

			for(int j = 0; j < 75; j++) {
				displayLocation.add(stepVector);
				Location location = displayLocation.clone();

				new BukkitRunnable() {
					int ticks = 0;

					@Override
					public void run() {

						particle.display(Misc.getNearbyRealPlayers(location, 50), location, ParticleColor.RED);
						ticks += 5;
						if(ticks >= 15) cancel();
					}
				}.runTaskTimer(PitSim.INSTANCE, 75 - j, 5);
			}
		}
	}
}
