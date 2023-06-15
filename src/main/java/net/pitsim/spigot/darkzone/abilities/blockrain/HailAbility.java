package net.pitsim.spigot.darkzone.abilities.blockrain;

import net.pitsim.spigot.controllers.DamageManager;
import net.pitsim.spigot.misc.BlockData;
import net.pitsim.spigot.misc.Misc;
import net.pitsim.spigot.misc.Sounds;
import net.pitsim.spigot.misc.effects.FallingBlock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.LinkedHashMap;

public class HailAbility extends BlockRainAbility {
	public static LinkedHashMap<BlockData, Double> blockMap = getBlocks();

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

			if(material == Material.ICE) DamageManager.createIndirectAttack(getPitBoss().getBoss(), player, damage);
			else Misc.applyPotionEffect(player, PotionEffectType.SLOW, 20, 5, false, false);
		}
	}

	public static LinkedHashMap<BlockData, Double> getBlocks() {
		LinkedHashMap<BlockData, Double> blocks = new LinkedHashMap<>();
		blocks.put(new BlockData(Material.SNOW_BLOCK, (byte) 0), 1.0);
		blocks.put(new BlockData(Material.ICE, (byte) 0), 1.0);
		return blocks;
	}
}
