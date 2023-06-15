package net.pitsim.spigot.adarkzone.altar;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import net.pitsim.spigot.PitSim;
import net.minecraft.server.v1_8_R3.PacketPlayOutMapChunk;
import net.minecraft.server.v1_8_R3.PacketPlayOutMapChunkBulk;
import org.bukkit.Chunk;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BiomeChanger {
	
	public static int BIOME_ARRAY_LENGTH = 256;

	public static List<Chunk> chunkList = new ArrayList<>();

	private final HashMap<Biome, Byte> biomes = new HashMap<>();
	protected byte defaultBiomeId = 0;

	public BiomeChanger(PitSim instance) {
		biomes.put(Biome.FOREST, (byte) 4);

		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(instance, PacketType.Play.Server.MAP_CHUNK,
				PacketType.Play.Server.MAP_CHUNK_BULK) {

			@Override
			public void onPacketSending(PacketEvent event) {
				Biome biome = Biome.FOREST;
				setDefaultBiome(biome);
				Player player = event.getPlayer();
				PacketContainer packet = event.getPacket();
				PacketType type = packet.getType();

				if(type == PacketType.Play.Server.MAP_CHUNK) {
					PacketPlayOutMapChunk packetHandle = (PacketPlayOutMapChunk) packet.getHandle();

					Field x;
					Field z;

					try {
						x = packetHandle.getClass().getDeclaredField("a");
						x.setAccessible(true);
						z = packetHandle.getClass().getDeclaredField("b");
						z.setAccessible(true);
					} catch(NoSuchFieldException e) {
						throw new RuntimeException(e);
					}

					Chunk chunk;
					try {
						chunk = player.getWorld().getChunkAt(x.getInt(packetHandle), z.getInt(packetHandle));
					} catch(IllegalAccessException e) {
						throw new RuntimeException(e);
					}

					boolean found = false;
					for(Chunk storedChunk : chunkList) {
						if(storedChunk.getX() == chunk.getX() && storedChunk.getZ() == chunk.getZ()
								&& storedChunk.getWorld().equals(chunk.getWorld())) {
							found = true;
							break;
						}
					}

					if(found) translateMapChunk(packet, player, biome);
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

		Field x;
		Field z;

		try {
			x = PacketPlayOutMapChunkBulk.class.getDeclaredField("a");
			x.setAccessible(true);
			z = PacketPlayOutMapChunkBulk.class.getDeclaredField("b");
			z.setAccessible(true);
		} catch(NoSuchFieldException e) {
			throw new RuntimeException(e);
		}

		int dataStartIndex = 0;
		PacketPlayOutMapChunk.ChunkMap[] chunks = (PacketPlayOutMapChunk.ChunkMap[]) packet.getModifier().read(2);
		for(int chunkNum = 0; chunkNum < chunks.length; chunkNum++) {
			PacketPlayOutMapChunkBulk packetHandle = (PacketPlayOutMapChunkBulk) packet.getHandle();

			try {
				Chunk chunk = player.getWorld().getChunkAt(((int[]) x.get(packetHandle))[chunkNum], ((int[]) z.get(packetHandle))[chunkNum]);

				for(Chunk storedChunk : chunkList) {
					if(!storedChunk.getWorld().equals(chunk.getWorld())) continue;
					if(storedChunk.getX() == chunk.getX() && storedChunk.getZ() == chunk.getZ()) {
						ChunkInfo info = new ChunkInfo(player, chunks[chunkNum].b, 0, true, chunks[chunkNum].a, 0);
						if(info.data == null || info.data.length == 0) {
							info.data = chunks[chunkNum].a;
						} else {
							info.startIndex = dataStartIndex;
						}
						translateChunkInfo(info, biome);
						dataStartIndex += info.size;
					}
				}
			} catch(IllegalAccessException e) {
				throw new RuntimeException(e);
			}
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
