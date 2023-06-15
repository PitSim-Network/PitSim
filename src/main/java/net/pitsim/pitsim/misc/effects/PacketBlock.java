package net.pitsim.pitsim.misc.effects;

import net.pitsim.pitsim.PitSim;
import net.pitsim.pitsim.controllers.PacketManager;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PacketBlock {
	private final Material material;
	private List<Player> viewers = new ArrayList<>();
	private final Location spawnLocation;
	private final World nmsWorld;
	private final byte blockData;

	private final IBlockData originalBlockData;

	private boolean isRemoved;

	public PacketBlock(Material material, byte data, Location location, List<Player> viewers) {
		this(material, data, location);
		this.viewers = viewers;
	}

	public PacketBlock(Material material, byte data, Location spawnLocation) {
		this.material = material;
		this.spawnLocation = spawnLocation.clone();
		this.nmsWorld = ((CraftWorld) spawnLocation.getWorld()).getHandle();
		this.blockData = data;

		Block originalBlock = spawnLocation.getBlock();
		originalBlockData = CraftMagicNumbers.getBlock(originalBlock.getType()).fromLegacyData(originalBlock.getData() & 255);
	}

	public PacketBlock spawnBlock() {
		IBlockData data = CraftMagicNumbers.getBlock(material).fromLegacyData(blockData & 255);
		BlockPosition blockPosition = new BlockPosition(spawnLocation.getBlockX(), spawnLocation.getBlockY(), spawnLocation.getBlockZ());
		PacketPlayOutBlockChange packet = new PacketPlayOutBlockChange(nmsWorld, blockPosition);

		try {
			Field field = packet.getClass().getField("block");
			field.setAccessible(true);
			field.set(packet, data);

		} catch(NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}

		for(Player viewer : viewers) {
			EntityPlayer nmsPlayer = ((CraftPlayer) viewer).getHandle();
			nmsPlayer.playerConnection.sendPacket(packet);
		}

		PacketManager.suppressedLocations.put(this, viewers);
		return this;
	}

	public PacketBlock removeAfter(int ticks) {
		new BukkitRunnable() {
			@Override
			public void run() {
				removeBlock();
			}
		}.runTaskLater(PitSim.INSTANCE, ticks);
		return this;
	}

	public void removeBlock() {
		if(isRemoved) return;
		isRemoved = true;

		BlockPosition blockPosition = new BlockPosition(spawnLocation.getBlockX(), spawnLocation.getBlockY(), spawnLocation.getBlockZ());
		PacketPlayOutBlockChange packet = new PacketPlayOutBlockChange(nmsWorld, blockPosition);

		PacketManager.suppressedLocations.remove(this);

		try {
			Field field = packet.getClass().getField("block");
			field.setAccessible(true);
			field.set(packet, originalBlockData);

		} catch(NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}

		for(Player viewer : viewers) {
			EntityPlayer nmsPlayer = ((CraftPlayer) viewer).getHandle();
			nmsPlayer.playerConnection.sendPacket(packet);
		}
	}

	public Material getMaterial() {
		return material;
	}

	public List<Player> getViewers() {
		return viewers;
	}

	public Location getLocation() {
		return spawnLocation;
	}

	public byte getBlockData() {
		return blockData;
	}

	public World getNmsWorld() {
		return nmsWorld;
	}

	public IBlockData getOriginalBlockData() {
		return originalBlockData;
	}

	public boolean isRemoved() {
		return isRemoved;
	}

	public PacketBlock setViewers(Player viewer) {
		this.viewers = Collections.singletonList(viewer);
		return this;
	}

	public PacketBlock setViewers(List<Player> viewers) {
		this.viewers = viewers;
		return this;
	}
}
