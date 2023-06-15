package net.pitsim.spigot.adarkzone.abilities.blockrain;

import net.pitsim.spigot.controllers.DamageManager;
import net.pitsim.spigot.misc.BlockData;
import net.pitsim.spigot.misc.Sounds;
import net.pitsim.spigot.misc.effects.FallingBlock;
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
		if(Math.random() < 0.2) Sounds.ANVIL_RAIN.play(location, 20);
		for(Entity nearbyEntity : location.getWorld().getNearbyEntities(location, 1.5, 1.5, 1.5)) {
			if(!(nearbyEntity instanceof Player)) continue;
			Player player = Bukkit.getPlayer(nearbyEntity.getUniqueId());
			if(player == null) continue;

			DamageManager.createIndirectAttack(getPitBoss().getBoss(), player, damage);
		}
	}
}
