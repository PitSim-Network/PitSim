package dev.kyro.pitsim.adarkzone.abilities;

import dev.kyro.pitsim.adarkzone.RoutinePitBossAbility;
import dev.kyro.pitsim.cosmetics.ParticleOffset;
import dev.kyro.pitsim.cosmetics.PitParticle;
import dev.kyro.pitsim.cosmetics.particles.BlockCrackParticle;
import dev.kyro.pitsim.misc.Sounds;
import dev.kyro.pitsim.misc.effects.FallingBlock;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class PoundAbility extends RoutinePitBossAbility {
	public int radius;

	public PoundAbility(double routineWeight, int radius) {
		super(routineWeight);
		this.radius = radius;
	}

	@Override
	public void onRoutineExecute() {
		Location centerLocation = pitBoss.boss.getLocation().clone().subtract(0, 1, 0);

		List<Block> applicableBlocks = new ArrayList<>();

		for(int x = -5; x < 6; x++) {
			for(int z = -5; z < 6; z++) {
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

		List<Player> viewers = new ArrayList<>();
		for(Entity entity : pitBoss.boss.getNearbyEntities(50, 50, 50)) {
			if(!(entity instanceof Player)) return;
			Player player = Bukkit.getPlayer(entity.getUniqueId());
			if(player != null) viewers.add(player);
		}


		for(Block block : applicableBlocks) {
//			Vector vector = block.getLocation().toVector().subtract(centerLocation.toVector());
//			vector.setY(vector.getY() + 1.5);
//			vector.normalize();

			Vector vector = new Vector(0, 0.6, 0);

			FallingBlock fallingBlock = new FallingBlock(block.getType(), block.getData(), block.getLocation().add(0, 1, 0));
			fallingBlock.setViewers(viewers);
			fallingBlock.spawnBlock();
			fallingBlock.removeAfter(25);
			fallingBlock.setVelocity(vector);
		}

		PitParticle particle = new BlockCrackParticle(false, false, new MaterialData(Material.DIRT));

		for(Player viewer : viewers) {
			EntityPlayer nmsPlayer = ((CraftPlayer) viewer).getHandle();

			for(int i = 0; i < 100; i++) {
				particle.display(nmsPlayer, centerLocation, new ParticleOffset(5, 5, 5), null);
			}


			if(viewer.getLocation().distance(pitBoss.boss.getLocation()) > radius + 2) continue;

			Vector vector = new Vector(0, 0.6, 0);
			vector.setY(vector.getY() + 1.5);
			vector.normalize();

			viewer.setVelocity(vector);
		}
		//TODO: Use proximity instead of damage map

		Sounds.EXTRACT.play(pitBoss.boss.getLocation());
	}
}

