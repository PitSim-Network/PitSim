package dev.kyro.pitsim.adarkzone.abilities;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.PitBossAbility;
import dev.kyro.pitsim.controllers.DamageManager;
import dev.kyro.pitsim.enums.PitEntityType;
import dev.kyro.pitsim.misc.BlockData;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import dev.kyro.pitsim.misc.effects.FallingBlock;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PopupAbility extends PitBossAbility {
	public BlockData blockData;
	public double damage;
	public int radius;
	public int count;

	public PopupAbility(double routineWeight, BlockData blockData, double damage, int radius, int count) {
		super(routineWeight);
		this.blockData = blockData;
		this.damage = damage;
		this.radius = radius;
		this.count = count;
	}

	@Override
	public void onRoutineExecute() {
		if(getPitBoss().bossTargetingSystem.target == null) return;
		spawnPopups(getPitBoss().bossTargetingSystem.target.getLocation());
	}

	public void spawnPopups(Location centerLocation) {
		List<Block> applicableBlocks = new ArrayList<>();

		for(int x = -1 * radius; x < radius + 1; x++) {
			for(int z = -1 * radius; z < radius + 1; z++) {
				Location blockLocation = centerLocation.clone().add(x, 0, z);

				if(blockLocation.distance(centerLocation) > radius) continue;

				if(blockLocation.getBlock().getType() != Material.AIR && blockLocation.clone().add(0, 1, 0).getBlock().getType() == Material.AIR) {
					applicableBlocks.add(blockLocation.getBlock());
					continue;
				}

				for(int i = -2; i < 3; i++) {
					Location checkPosition = blockLocation.clone().add(0, i, 0);
					if(checkPosition.getBlock().getType() == Material.AIR || checkPosition.clone().add(0, 1, 0).getBlock().getType() != Material.AIR)
						continue;
					applicableBlocks.add(checkPosition.getBlock());
				}
			}
		}

		List<Location> usedLocations = new ArrayList<>();
		Random random = new Random();

		for(int i = 0; i < count; i++) {
			int index = random.nextInt(applicableBlocks.size());
			Location spawnLocation = applicableBlocks.get(index).getLocation();
			if(usedLocations.contains(spawnLocation)) {
				i--;
				continue;
			}
			usedLocations.add(spawnLocation);
		}

		for(int i = 0; i < usedLocations.size(); i++) {
			Block block = usedLocations.get(i).getBlock();
			int delay = i * 2;
			int baseTime = count > 20 ? 40 + count : 40;

			new BukkitRunnable() {
				@Override
				public void run() {
					new Popup(5, block.getLocation());
				}
			}.runTaskLater(PitSim.INSTANCE, delay);
		}

	}

	public class Popup {
		public FallingBlock fallingBlock;
		public double height;
		public Location spawnLocation;

		public Popup(int height, Location spawnLocation) {
			this.height = height;
			this.spawnLocation = spawnLocation;

			this.fallingBlock = new FallingBlock(blockData, spawnLocation);
			fallingBlock.setViewers(getViewers());
			fallingBlock.spawnBlock();

			Location top = spawnLocation.clone().add(0, height, 0);
			Vector vector = top.toVector().subtract(spawnLocation.toVector()).normalize().multiply(0.75);
			fallingBlock.setVelocity(vector);

			fallingBlock.removeAfter(Misc.getFallTime(height) * 2);
			checkForDamage();
			Sounds.POPUP.play(spawnLocation);

			new BukkitRunnable() {
				@Override
				public void run() {
					checkForDamage();
				}
			}.runTaskLater(PitSim.INSTANCE, Misc.getFallTime(height) * 2L);

		}

		public void checkForDamage() {
			spawnLocation.getWorld().getNearbyEntities(spawnLocation, 1.5, 1.5, 1.5).forEach(entity -> {
				if(Misc.isEntity(entity, PitEntityType.REAL_PLAYER)) {
					Player player = (Player) entity;
					setFire(player);
				}
			});
		}

		public void setFire(Player player) {
			if(player.getFireTicks() > 0) return;
			player.setFireTicks(5 * 20);
			new BukkitRunnable() {
				int i = 0;
				@Override
				public void run() {
					if(++i >= 5) cancel();
					DamageManager.createIndirectAttack(getPitBoss().boss, player, damage);
				}
			}.runTaskTimer(PitSim.INSTANCE, 0, 20);
		}
	}
}

