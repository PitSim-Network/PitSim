package dev.kyro.pitsim.adarkzone.abilities;

import dev.kyro.pitsim.adarkzone.abilities.abilitytypes.BlockRainAbility;
import dev.kyro.pitsim.controllers.DamageManager;
import dev.kyro.pitsim.misc.BlockData;
import dev.kyro.pitsim.misc.Sounds;
import dev.kyro.pitsim.misc.effects.FallingBlock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class AnvilRainAbility extends BlockRainAbility {
	public AnvilRainAbility(double routineWeight, int radius, int blockCount, double damage) {
		super(routineWeight, radius, blockCount, new BlockData(Material.ANVIL, (byte) 0), damage);
	}

	@Override
	public void onBlockLand(FallingBlock fallingBlock, Location location) {
		Sounds.ANVIL_RAIN.play(location, 20);
		for(Entity nearbyEntity : location.getWorld().getNearbyEntities(location, 1.5, 1.5, 1.5)) {
			if(!(nearbyEntity instanceof Player)) continue;
			Player player = Bukkit.getPlayer(nearbyEntity.getUniqueId());
			if(player == null) continue;

			DamageManager.createAttack(getPitBoss().boss, player, damage);
		}
	}
}
