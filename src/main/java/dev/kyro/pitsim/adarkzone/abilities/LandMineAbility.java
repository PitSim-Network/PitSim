package dev.kyro.pitsim.adarkzone.abilities;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.PitBossAbility;
import dev.kyro.pitsim.controllers.DamageManager;
import dev.kyro.pitsim.cosmetics.particles.ExplosionHugeParticle;
import dev.kyro.pitsim.cosmetics.particles.ParticleColor;
import dev.kyro.pitsim.cosmetics.particles.RedstoneParticle;
import dev.kyro.pitsim.enums.PitEntityType;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import dev.kyro.pitsim.misc.effects.FallingBlock;
import dev.kyro.pitsim.misc.math.RotationUtils;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LandMineAbility extends PitBossAbility {
	public static final double SHAKE_THRESHOLD = 0.02;
	public long disableTicks;
	public double radius;
	public long removeTicks;
	public double damage;
	public List<LandMine> landMines = new ArrayList<>();

	public List<LandMine> toRemove = new ArrayList<>();

	public LandMineAbility(double routineWeight, double radius, long primeTicks, long removeTicks, double damage) {
		super(routineWeight);

		this.radius = radius;
		this.disableTicks = primeTicks;
		this.removeTicks = removeTicks;
		this.damage = damage;
	}

	@Override
	public void onRoutineExecute() {
		LandMine landMine = new LandMine(new FallingBlock(Material.TNT, (byte) 0, getPitBoss().getBoss().getLocation().add(0, 0.5, 0)));
		FallingBlock fallingBlock = landMine.fallingBlock;
		fallingBlock.setViewers(getViewers());
		fallingBlock.spawnBlock();
		landMines.add(landMine);

		Sounds.TNT_PLACE.play(getPitBoss().getBoss().getLocation(), 20);

		landMine.runnable = new BukkitRunnable() {
			int i = 0;

			@Override
			public void run() {
				if(i == 0) {
					landMine.isStarting = false;
				}

				int dir = i % 4;
				if(dir == 0) spawnCircle(landMine.fallingBlock.getSpawnLocation());

				Vector vector;
				switch(dir) {
					case 0:
						vector = new Vector(0, SHAKE_THRESHOLD * 4, SHAKE_THRESHOLD);
						break;
					case 1:
						vector = new Vector(SHAKE_THRESHOLD, SHAKE_THRESHOLD * 4, 0);
						break;
					case 2:
						vector = new Vector(0, 0, -1 * SHAKE_THRESHOLD);
						break;
					case 3:
						vector = new Vector(-1 * SHAKE_THRESHOLD, 0, 0);
						break;
					default:
						vector = new Vector(0, 0, 0);
						break;
				}

				RotationUtils.rotate(vector, landMine.rotation, 0 , 0);
				landMine.fallingBlock.setVelocity(vector);
				if(i >= removeTicks) {
					this.cancel();
					landMine.fallingBlock.removeBlock();
					landMines.remove(landMine);
				}
				i++;
			}
		}.runTaskTimer(PitSim.INSTANCE, disableTicks, 2);

	}

	public void spawnCircle(Location center) {
		RedstoneParticle particle = new RedstoneParticle(false, false);
		List<Player> viewers = getViewers();

		for(int degrees = 0; degrees < 360; degrees++) {
			double x = Math.cos(Math.toRadians(degrees)) * radius;
			double z = Math.sin(Math.toRadians(degrees)) * radius;

			for(Player viewer : viewers) {
				EntityPlayer nmPlayer = ((CraftPlayer) viewer).getHandle();
				particle.display(nmPlayer, center.clone().add(x, -0.4, z), ParticleColor.DARK_RED);
			}
		}
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if(player.getWorld() != getPitBoss().getBoss().getWorld()) return;

		for(LandMine landMine : landMines) {
			if(landMine.runnable == null || landMine.isStarting) continue;

			double distance = player.getLocation().distance(landMine.fallingBlock.getSpawnLocation());
			if(distance > radius) continue;

			detonate(landMine);
		}

		landMines.removeAll(toRemove);
		toRemove.clear();

	}

	public static class LandMine {
		public FallingBlock fallingBlock;
		public boolean isStarting = true;
		public BukkitTask runnable;
		public int rotation = new Random().nextInt(360);

		public LandMine(FallingBlock fallingBlock) {
			this.fallingBlock = fallingBlock;
		}
	}

	@Override
	public void disable() {
		super.disable();

		for(LandMine landMine : new ArrayList<>(landMines)) {
			detonate(landMine);
		}
	}

	public void detonate(LandMine landMine) {
		landMine.runnable.cancel();
		landMine.fallingBlock.removeBlock();
		toRemove.add(landMine);

		ExplosionHugeParticle particle = new ExplosionHugeParticle();
		Sounds.CREEPER_EXPLODE.play(landMine.fallingBlock.getSpawnLocation(), 40);

		FallingBlock block = landMine.fallingBlock;
		Location center = block.getSpawnLocation();
		for(Entity entity : center.getWorld().getNearbyEntities(center, radius, radius, radius)) {
			if(!Misc.isEntity(entity, PitEntityType.REAL_PLAYER)) continue;
			if(!Misc.isEntity(entity, PitEntityType.REAL_PLAYER)) continue;
			Player player = (Player) entity;

			double distance = player.getLocation().distance(center);
			if(distance > radius) continue;

			DamageManager.createIndirectAttack(getPitBoss().getBoss(), player, damage);

			double multiplier = Math.pow(5 - distance, 1.5);
			Vector velocity = player.getLocation().toVector().subtract(landMine.fallingBlock.getSpawnLocation().toVector());
			velocity.add(new Vector(0, 0.1, 0));
			velocity.normalize();
			velocity.multiply(1.5);
			velocity.multiply(multiplier);
			player.setVelocity(velocity);
		}

		for(Player viewer : getViewers()) {
			EntityPlayer nmsPlayer = ((CraftPlayer) viewer).getHandle();
			particle.display(nmsPlayer, landMine.fallingBlock.getSpawnLocation());
		}
	}
}
