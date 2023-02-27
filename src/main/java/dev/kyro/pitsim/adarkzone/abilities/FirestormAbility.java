package dev.kyro.pitsim.adarkzone.abilities;

import dev.kyro.pitsim.adarkzone.abilities.abilitytypes.BlockRainAbility;
import dev.kyro.pitsim.misc.BlockData;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import dev.kyro.pitsim.misc.effects.FallingBlock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

public class FirestormAbility extends BlockRainAbility {
	public static Map<BlockData, Double> blockMap = getBlocks();

	public FirestormAbility(double routineWeight, int radius, int blockCount, double damage) {
		super(routineWeight, radius, blockCount, blockMap, damage);
	}

	@Override
	public void onBlockLand(FallingBlock fallingBlock, Location location) {
		Sounds.SNAKE_ICE.play(location, 20);
		Material material = fallingBlock.getMaterial();

		for(Entity nearbyEntity : location.getWorld().getNearbyEntities(location, 1, 1, 1)) {
			if(!(nearbyEntity instanceof Player)) continue;
			Player player = Bukkit.getPlayer(nearbyEntity.getUniqueId());
			if(player == null) continue;

			if(material == Material.FIRE) player.damage(damage, pitBoss.boss);
			else Misc.applyPotionEffect(player, PotionEffectType.SLOW, 20, 5, false, false);
		}
	}

	public static Map<BlockData, Double> getBlocks() {
		Map<BlockData, Double> blocks = new HashMap<>();
		blocks.put(new BlockData(Material.FIRE, (byte) 0), 1.0);
		blocks.put(new BlockData(Material.NETHER_BRICK, (byte) 0), 1.0);
		return blocks;
	}
}
