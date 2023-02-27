package dev.kyro.pitsim.adarkzone.abilities;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.RoutinePitBossAbility;
import dev.kyro.pitsim.cosmetics.RotationTools;
import dev.kyro.pitsim.cosmetics.particles.ExplosionHugeParticle;
import dev.kyro.pitsim.cosmetics.particles.ParticleColor;
import dev.kyro.pitsim.cosmetics.particles.RedstoneParticle;
import dev.kyro.pitsim.misc.Sounds;
import dev.kyro.pitsim.misc.effects.FallingBlock;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Bukkit;
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

public class LandMineAbility extends RoutinePitBossAbility {
	public double shakeThreshold = 0.02;
	public long disableTicks = 20;
	public double radius = 2;
	public List<LandMine> landMines = new ArrayList<>();

	public LandMineAbility(double routineWeight) {
		super(routineWeight);
	}

	@Override
	public void onRoutineExecute() {
		LandMine landMine = new LandMine(new FallingBlock(Material.TNT, (byte) 0, pitBoss.boss.getLocation().add(0, 0.5, 0)));
		FallingBlock fallingBlock = landMine.fallingBlock;
		fallingBlock.setViewers(getViewers());
		fallingBlock.spawnBlock();
		landMines.add(landMine);

		Sounds.TNT_PLACE.play(pitBoss.boss.getLocation(), 20);

		landMine.runnable = new BukkitRunnable() {
			int i = 0;

			@Override
			public void run() {
				if(i == 0) {
					landMine.isStarting = false;
					Sounds.TNT_PRIME.play(pitBoss.boss.getLocation(), 20);
				}

				int dir = i % 4;
				if(dir == 0) spawnCircle(landMine.fallingBlock.getSpawnLocation());

				Vector vector;
				switch(dir) {
					case 0:
						vector = new Vector(0, shakeThreshold * 4, shakeThreshold);
						break;
					case 1:
						vector = new Vector(shakeThreshold, shakeThreshold * 4, 0);
						break;
					case 2:
						vector = new Vector(0, 0, -1 * shakeThreshold);
						break;
					case 3:
						vector = new Vector(-1 * shakeThreshold, 0, 0);
						break;
					default:
						vector = new Vector(0, 0, 0);
						break;
				}

				RotationTools.rotate(vector, landMine.rotation, 0 , 0);
				landMine.fallingBlock.setVelocity(vector);
				if(i == 200) {
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
		if(player.getWorld() != pitBoss.boss.getWorld()) return;

		for(LandMine landMine : landMines) {
			if(landMine.runnable == null || landMine.isStarting) continue;

			double distance = player.getLocation().distance(landMine.fallingBlock.getSpawnLocation());
			if(distance > radius) continue;

			detonate(landMine);
		}

	}

	public List<Player> getViewers() {
		List<Player> viewers = new ArrayList<>();
		for(Entity entity : pitBoss.boss.getNearbyEntities(50, 50, 50)) {
			if(!(entity instanceof Player)) continue;
			Player player = Bukkit.getPlayer(entity.getUniqueId());
			if(player != null) viewers.add(player);
		}
		return viewers;
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

		for(LandMine landMine : landMines) {
			detonate(landMine);
		}
	}

	public void detonate(LandMine landMine) {
		landMine.runnable.cancel();
		landMine.fallingBlock.removeBlock();
		landMines.remove(landMine);

		ExplosionHugeParticle particle = new ExplosionHugeParticle();
		Sounds.CREEPER_EXPLODE.play(landMine.fallingBlock.getSpawnLocation(), 40);

		FallingBlock block = landMine.fallingBlock;
		Location center = block.getSpawnLocation();
		for(Entity entity : center.getWorld().getNearbyEntities(center, radius, radius, radius)) {
			if(!(entity instanceof Player) || entity == pitBoss.boss) continue;
			Player player = (Player) entity;

			double distance = player.getLocation().distance(center);
			if(distance > radius) continue;

			player.damage(500, pitBoss.boss);

			double multiplier = Math.pow(5 - distance, 1.5);
			Vector playerVector = player.getLocation().toVector().subtract(landMine.fallingBlock.getSpawnLocation().toVector());
			playerVector.add(new Vector(0, 0.1, 0));
			playerVector.normalize();
			playerVector.multiply(1.5);
			playerVector.multiply(multiplier);
			player.setVelocity(playerVector);
		}

		for(Player viewer : getViewers()) {
			EntityPlayer nmsPlayer = ((CraftPlayer) viewer).getHandle();
			particle.display(nmsPlayer, landMine.fallingBlock.getSpawnLocation());
		}
	}
}
