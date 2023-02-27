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

public class HailAbility extends BlockRainAbility {
	public static Map<BlockData, Double> blockMap = getBlocks();

	public HailAbility(double routineWeight, int radius, int blockCount, double damage) {
		super(routineWeight, radius, blockCount, blockMap, damage);
	}

	@Override
	public void onBlockLand(FallingBlock fallingBlock, Location location) {
		Sounds.SNAKE_ICE.play(location, 20);
		Material material = fallingBlock.getMaterial();

		for(Entity nearbyEntity : location.getWorld().getNearbyEntities(location, 1.5, 1.5, 1.5)) {
			if(!(nearbyEntity instanceof Player)) continue;
			Player player = Bukkit.getPlayer(nearbyEntity.getUniqueId());
			if(player == null) continue;

			if(material == Material.ICE) player.damage(damage, pitBoss.boss);
			else Misc.applyPotionEffect(player, PotionEffectType.SLOW, 20, 5, false, false);
		}
	}

	public static Map<BlockData, Double> getBlocks() {
		Map<BlockData, Double> blocks = new HashMap<>();
		blocks.put(new BlockData(Material.SNOW_BLOCK, (byte) 0), 1.0);
		blocks.put(new BlockData(Material.ICE, (byte) 0), 1.0);
		return blocks;
	}
}
