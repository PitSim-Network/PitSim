package dev.kyro.pitsim.adarkzone.abilities;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.PitBossAbility;
import dev.kyro.pitsim.adarkzone.RoutinePitBossAbility;
import dev.kyro.pitsim.misc.effects.FallingBlock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlockRainAbility extends RoutinePitBossAbility {

	public int radius;
	public int blockCount;
	public Material material;
	public byte data;

	public BlockRainAbility(double routineWeight, int radius, int blockCount, Material material, byte data) {
		super(routineWeight);
		this.radius = radius;
		this.blockCount = blockCount;
		this.material = material;
		this.data = data;
	}

	@Override
	public void onRoutineExecute() {

		Location centerLocation = pitBoss.boss.getLocation().clone().subtract(0, 1, 0);
		List<Location> applicableLocations = new ArrayList<>();

		for(int x = -1 * radius; x < radius + 1; x++) {
			for(int z = -1 * radius; z < radius + 1; z++) {
				Location blockLocation = centerLocation.clone().add(x, 0, z);

				if(blockLocation.distance(centerLocation) > radius) continue;

				if(blockLocation.getBlock().getType() != Material.AIR && blockLocation.clone().add(0, 1, 0).getBlock().getType() == Material.AIR) {
					applicableLocations.add(blockLocation);
					continue;
				}

				for(int i = -2; i < 3; i++) {
					Location checkPosition = blockLocation.clone().add(0, i, 0);
					if(checkPosition.getBlock().getType() == Material.AIR || checkPosition.clone().add(0, 1, 0).getBlock().getType() != Material.AIR)
						continue;
					applicableLocations.add(checkPosition);
				}
			}
		}

		List<Location> usedLocations = new ArrayList<>();
		Random random = new Random();

		for(int i = 0; i < blockCount; i++) {
			Location randomLocation = applicableLocations.get(random.nextInt(applicableLocations.size()));
			if(usedLocations.contains(randomLocation)) {
				i--;
				continue;
			}

			usedLocations.add(randomLocation);
			int maxHeight = getMaxHeight(randomLocation);
			int addedHeight = random.nextInt(maxHeight);
			randomLocation.add(0, addedHeight, 0);

			new BukkitRunnable() {
				@Override
				public void run() {
					FallingBlock fallingBlock = new FallingBlock(material, data, randomLocation);
					fallingBlock.setViewers(getViewers());
					fallingBlock.spawnBlock();

					//1000 - 5.102d + 989.960 W-1(-0.36786e0.0051538d)

					double time = 1000 - (5.102 * addedHeight) + 989.960 * lambertW(Math.pow(-0.36786, Math.log(0.0051538 * addedHeight)));

				}
			}.runTaskLater(PitSim.INSTANCE, i % 10);

		}
	}


	public int getMaxHeight(Location location) {
		Block[] blocks = new Block[5];

		for(int i = 0; i < 5; i++) blocks[i] = location.clone().add(0, i + 1, 0).getBlock();

		int solidIndex = -1;
		for(int i = 0; i < 5; i++) {
			if(blocks[i].getType() != Material.AIR) {
				solidIndex = i;
				break;
			}
		}

		return solidIndex + 1;
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

	public static double lambertW(double x) {
		double eps = 1e-8; // tolerance for convergence
		double w = Math.log(Math.max(x, 1e-16)); // initial guess

		while (true) {
			double ew = Math.exp(w);
			double f = w * ew - x;
			double df = (w + 1) * ew;
			double delta = f / df;
			w -= delta;
			if (Math.abs(delta) < eps) {
				break;
			}
		}

		return w;
	}
}
