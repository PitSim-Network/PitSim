package dev.kyro.pitsim.adarkzone.altar;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import dev.kyro.pitsim.PitSim;
import net.minecraft.server.v1_8_R3.PacketPlayOutMapChunk;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.v1_8_R3.CraftChunk;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import net.minecraft.server.v1_8_R3.BiomeDesert;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class BiomeChanger {
	
	public static int BIOME_ARRAY_LENGTH = 256;

	public static List<Chunk> chunkList = new ArrayList<>();

	private final HashMap<Biome, Byte> biomes = new HashMap<>();
	protected byte defaultBiomeId = 0;

	public BiomeChanger(PitSim instance) {
		biomes.put(Biome.DESERT, (byte) 2);

		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(instance, PacketType.Play.Server.MAP_CHUNK, PacketType.Play.Server.MAP_CHUNK_BULK) {

			@Override
			public void onPacketSending(PacketEvent event) {
				Biome biome = Biome.DESERT;
				setDefaultBiome(biome);
				Player player = event.getPlayer();
				PacketContainer packet = event.getPacket();
				PacketType type = packet.getType();
				if(type == PacketType.Play.Server.MAP_CHUNK) {
					translateMapChunk(packet, player, biome);
				}
				else if(type == PacketType.Play.Server.MAP_CHUNK_BULK) {
					translateMapChunkBulk(packet, player, biome);
				}
			}

		});
	}

	protected void translateMapChunk(PacketContainer packet, Player player, Biome biome) {
		PacketPlayOutMapChunk.ChunkMap chunk = (PacketPlayOutMapChunk.ChunkMap)packet.getModifier().read(2);
		if(chunk.a != null) {
			translateChunkInfo(new ChunkInfo(player, chunk.b, 0, getOrDefault(packet.getBooleans().readSafely(0), true), chunk.a, 0), biome);
		}
	}

	protected void translateMapChunkBulk(PacketContainer packet, Player player, Biome biome) {
		int dataStartIndex = 0;
		PacketPlayOutMapChunk.ChunkMap[] chunks = (PacketPlayOutMapChunk.ChunkMap[])packet.getModifier().read(2);
		for(int chunkNum = 0; chunkNum < chunks.length; chunkNum++) {
			ChunkInfo info = new ChunkInfo(player, chunks[chunkNum].b, 0, true, chunks[chunkNum].a, 0);
			if(info.data == null || info.data.length == 0) {
				info.data = chunks[chunkNum].a;
			}
			else {
				info.startIndex = dataStartIndex;
			}
			translateChunkInfo(info, biome);
			dataStartIndex += info.size;
		}
	}

	protected void translateChunkInfo(ChunkInfo info, Biome replacement) {
		if(info.hasContinous) {
			int biomeStart = info.data.length - BIOME_ARRAY_LENGTH;
			for(int i = biomeStart; i < info.data.length; i++) {
				Biome biome = this.getBiomeByID(info.data[i]);
				if(biome == null) {
					info.data[i] = defaultBiomeId;
					continue;
				}
				info.data[i] = replacement == null ? defaultBiomeId : this.getBiomeID(replacement);
			}
		}
	}

	public static void refreshChunk(World world, Chunk chunk) {
		PacketPlayOutMapChunk packet = new PacketPlayOutMapChunk(((CraftChunk)chunk).getHandle(), true, 0xffff);

		int absChunkX = Math.abs(chunk.getX());
		int absChunkZ = Math.abs(chunk.getX());

		int viewDistance = Bukkit.getViewDistance();
		for(Player player : chunk.getWorld().getPlayers()) {
			Location location = player.getLocation();
			int absX = absChunkX - Math.abs(location.getBlockX());
			int absZ = absChunkZ - Math.abs(location.getBlockZ());

			if((absX >= 0 && absX <= viewDistance) || (absZ >= 0 && absZ <= viewDistance)) {
				((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
			}
		}
	}

	public Biome getBiomeByID(byte id) {
		for(Map.Entry<Biome, Byte> entry : biomes.entrySet()) {
			if(entry.getValue().equals(id)) {
				return entry.getKey();
			}
		}
		return null;
	}

	public Byte getBiomeID(Biome biome) {
		return biomes.get(biome);
	}

	public void setDefaultBiome(Biome biome) {
		defaultBiomeId = biomes.get(biome);
	}

	protected <T> T getOrDefault(T value, T defaultIfNull) {
		return value != null ? value : defaultIfNull;
	}

	protected static class ChunkInfo {

		public Player player;
		public int chunkMask;
		public int extraMask;
		public boolean hasContinous;
		public byte[] data;
		public int startIndex;
		public int size;

		public ChunkInfo(Player player, int chunkMask, int extraMask, boolean hasContinous, byte[] data, int startIndex) {
			this.player = player;
			this.chunkMask = chunkMask;
			this.extraMask = extraMask;
			this.hasContinous = hasContinous;
			this.data = data;
			this.startIndex = startIndex;
		}

	}
}
