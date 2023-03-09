package dev.kyro.pitsim.adarkzone.abilities;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.schematic.MCEditSchematicFormat;
import com.sk89q.worldedit.world.DataException;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.PitBossAbility;
import dev.kyro.pitsim.misc.BlockData;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.SchematicPaste;
import dev.kyro.pitsim.misc.Sounds;
import dev.kyro.pitsim.misc.effects.FallingBlock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CageAbility extends PitBossAbility {
	public static Map<Location, EditSession> sessionMap = new HashMap<>();
	public int captureTicks;
	public int schemSize;

	public CageAbility(double routineWeight, int captureTicks, int schemSize) {
		super(routineWeight);

		this.captureTicks = captureTicks;
		this.schemSize = schemSize;
	}

	@Override
	public void onRoutineExecute() {
		File schematic = new File("plugins/WorldEdit/schematics/cage.schematic");

		viewers:
		for(Player viewer : getViewers()) {
			Location entryLocation = viewer.getLocation().add(-1, 0, -1);
			Sounds.CAGE.play(viewer);

			for(Location location : sessionMap.keySet()) {
				if(location.distance(viewer.getLocation()) < schemSize) continue viewers;
			}

			Misc.applyPotionEffect(viewer, PotionEffectType.SLOW, 20, 10, false, false);

			WorldEditPlugin worldEditPlugin = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
			EditSession session = worldEditPlugin.getWorldEdit().getEditSessionFactory().getEditSession(new BukkitWorld(viewer.getWorld()), 100000);
			sessionMap.put(entryLocation, session);


			Location location = viewer.getLocation().add(-1, 5, -1);

			Map<Location, BlockData> blockDataMap = SchematicPaste.getBlockMap(schematic, location);
			if(blockDataMap == null) continue;

			List<Location> locations = new ArrayList<>(blockDataMap.keySet());
			Location bottom = locations.get(locations.size() - 1);

			int height = 3;
			int ticks = Misc.getFallTime(height);

			for(Map.Entry<Location, BlockData> entry : blockDataMap.entrySet()) {
				FallingBlock fallingBlock = new FallingBlock(entry.getValue(), entry.getKey());
				fallingBlock.setViewers(getViewers());
				fallingBlock.spawnBlock();
				fallingBlock.removeAfter(ticks);
			}

			new BukkitRunnable() {
				@Override
				public void run() {
					try {
						Location paste = viewer.getLocation();
						CuboidClipboard clipboard = MCEditSchematicFormat.getFormat(schematic).load(schematic);
						clipboard.paste(session, new com.sk89q.worldedit.Vector(paste.getX(), paste.getY(), paste.getZ()), true);
					} catch(MaxChangedBlocksException | DataException | IOException e) {
						e.printStackTrace();
					}
				}
			}.runTaskLater(PitSim.INSTANCE, ticks);

			new BukkitRunnable() {
				@Override
				public void run() {
					session.undo(session);
					sessionMap.remove(entryLocation);
				}
			}.runTaskLater(PitSim.INSTANCE, ticks + captureTicks);
		}
	}
}
