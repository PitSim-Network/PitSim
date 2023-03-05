package dev.kyro.pitsim.misc.effects;

import dev.kyro.pitsim.PitSim;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FallingBlock {
	private final Material material;
	private List<Player> viewers = new ArrayList<>();
	private final Location spawnLocation;
	private final World nmsWorld;
	private final byte blockData;

	private EntityFallingBlock entityFallingBlock;

	public FallingBlock(Material material, byte data, Location location, List<Player> viewers) {
		this(material, data, location);
		this.viewers = viewers;
	}

	public FallingBlock(Material material, byte data, Location spawnLocation) {
		this.material = material;
		this.spawnLocation = spawnLocation;
		this.nmsWorld = ((CraftWorld) spawnLocation.getWorld()).getHandle();
		this.blockData = data;
	}

	public FallingBlock spawnBlock() {
		IBlockData data = CraftMagicNumbers.getBlock(material).fromLegacyData(blockData & 255);
		entityFallingBlock = new EntityFallingBlock(nmsWorld, 0, 0, 0, data);
		entityFallingBlock.setPosition(spawnLocation.getX(), spawnLocation.getY(), spawnLocation.getZ());

		PacketPlayOutSpawnEntity spawn = new PacketPlayOutSpawnEntity(entityFallingBlock, 70, Block.getCombinedId(entityFallingBlock.getBlock()));

		for(Player viewer : viewers) {
			((CraftPlayer) viewer).getHandle().playerConnection.sendPacket(spawn);
		}
		return this;
	}

	public FallingBlock setVelocity(Vector velocity) {
		double xMot = velocity.getX();
		double yMot = velocity.getY();
		double zMot = velocity.getZ();

		PacketPlayOutEntityVelocity packet = new PacketPlayOutEntityVelocity(entityFallingBlock.getId(), xMot, yMot, zMot);
		for(Player viewer : viewers) {
			((CraftPlayer) viewer).getHandle().playerConnection.sendPacket(packet);
		}
		return this;
	}

	public FallingBlock removeAfter(int ticks) {
		new BukkitRunnable() {
			@Override
			public void run() {
				removeBlock();
			}
		}.runTaskLater(PitSim.INSTANCE, ticks);
		return this;
	}

	public FallingBlock removeBlock() {
		PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(entityFallingBlock.getId());

		for(Player viewer : viewers) {
			((CraftPlayer) viewer).getHandle().playerConnection.sendPacket(destroy);
		}
		return this;
	}

	public Location getSpawnLocation() {
		return spawnLocation;
	}

	public Material getMaterial() {
		return material;
	}

	public List<Player> getViewers() {
		return viewers;
	}

	public FallingBlock setViewers(Player viewer) {
		this.viewers = Collections.singletonList(viewer);
		return this;
	}

	public FallingBlock setViewers(List<Player> viewers) {
		this.viewers = viewers;
		return this;
	}
}
