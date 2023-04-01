package dev.kyro.pitsim.adarkzone.abilities.blockrain;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.DamageManager;
import dev.kyro.pitsim.misc.BlockData;
import dev.kyro.pitsim.misc.Sounds;
import dev.kyro.pitsim.misc.effects.FallingBlock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class FirestormAbility extends BlockRainAbility {
	public static Map<BlockData, Double> blockMap = getBlocks();

	public FirestormAbility(double routineWeight, int radius, int blockCount, double damage) {
		super(routineWeight, radius, blockCount, blockMap, damage);
	}

	@Override
	public void onBlockLand(FallingBlock fallingBlock, Location location) {
		location.add(0.5, 0, 0.5);
		Material material = fallingBlock.getMaterial();

		if(material == Material.FIRE) Sounds.FIRE_EXTINGUISH.play(location, 20);
		else Sounds.BLOCK_LAND.play(location, 20);

		for(Entity nearbyEntity : location.getWorld().getNearbyEntities(location, 1.5, 1.5, 1.5)) {
			if(!(nearbyEntity instanceof Player)) continue;
			Player player = Bukkit.getPlayer(nearbyEntity.getUniqueId());
			if(player == null) continue;

			if(material == Material.FIRE) {
				if(player.getFireTicks() <= 0) {
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
			} else DamageManager.createIndirectAttack(getPitBoss().boss, player, damage * 3);
		}
	}

	public static Map<BlockData, Double> getBlocks() {
		Map<BlockData, Double> blocks = new HashMap<>();
		blocks.put(new BlockData(Material.FIRE, (byte) 0), 1.0);
		blocks.put(new BlockData(Material.NETHER_BRICK, (byte) 0), 1.0);
		return blocks;
	}
}
