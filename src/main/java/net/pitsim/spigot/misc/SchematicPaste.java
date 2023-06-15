package net.pitsim.spigot.misc;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.schematic.MCEditSchematicFormat;
import com.sk89q.worldedit.world.DataException;
import net.minecraft.server.v1_8_R3.NBTCompressedStreamTools;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Bat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class SchematicPaste {
	public static void loadSchematic(File file, Location location) {
		WorldEditPlugin worldEditPlugin = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
		EditSession session = worldEditPlugin.getWorldEdit().getEditSessionFactory().getEditSession(new BukkitWorld(location.getWorld()), 100000);
		try {
			CuboidClipboard clipboard = MCEditSchematicFormat.getFormat(file).load(file);
			clipboard.paste(session, new com.sk89q.worldedit.Vector(location.getX(), location.getY(), location.getZ()), false);
		} catch(MaxChangedBlocksException | DataException | IOException e) {
			e.printStackTrace();
		}
		Bat bat;
	}

	public static EditSession loadSchematicAir(File file, Location location) {
		WorldEditPlugin worldEditPlugin = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
		EditSession session = worldEditPlugin.getWorldEdit().getEditSessionFactory().getEditSession(new BukkitWorld(location.getWorld()), 100000);
		try {
			CuboidClipboard clipboard = MCEditSchematicFormat.getFormat(file).load(file);
			clipboard.paste(session, new com.sk89q.worldedit.Vector(location.getX(), location.getY(), location.getZ()), true);
		} catch(MaxChangedBlocksException | DataException | IOException e) {
			e.printStackTrace();
		}
		return session;
	}

	public static Map<Location, BlockData> getBlockMap(File schematic, Location spawnLocation) {
		try {
			Map<Location, BlockData> blockMap = new HashMap<>();

			InputStream fis = Files.newInputStream(schematic.toPath());
			NBTTagCompound nbtData = NBTCompressedStreamTools.a(fis);

			short width = nbtData.getShort("Width");
			short height = nbtData.getShort("Height");
			short length = nbtData.getShort("Length");

			byte[] blocks = nbtData.getByteArray("Blocks");
			byte[] data = nbtData.getByteArray("Data");

			for(int y = 0; y < height; y++) {
				for(int z = 0; z < length; z++) {
					for(int x = 0; x < width; x++) {

						Location location = spawnLocation.clone().add(x, y, z);
						int index = x + (y * length + z) * width;
						BlockData blockData = new BlockData(Material.getMaterial(blocks[index]), data[index]);
						blockMap.put(location, blockData);
					}
				}
			}

			fis.close();
			return blockMap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
