package dev.kyro.pitsim.adarkzone.abilities;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.PitBossAbility;
import dev.kyro.pitsim.misc.BlockData;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.SchematicPaste;
import dev.kyro.pitsim.misc.Sounds;
import dev.kyro.pitsim.misc.effects.FallingBlock;
import dev.kyro.pitsim.misc.effects.PacketBlock;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;

public class CageAbility extends PitBossAbility {
	public static Map<Player, List<PacketBlock>> packetBlockMap = new HashMap<>();
	public int captureTicks;
	public int schemSize;
	public List<UUID> capturedPlayers = new ArrayList<>();

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

			for(Player player : packetBlockMap.keySet()) {
				if(player.getLocation().distance(viewer.getLocation()) < schemSize) continue viewers;
			}

			Misc.applyPotionEffect(viewer, PotionEffectType.SLOW, 20, 10, false, false);

			Location trueSpawnLoc = viewer.getLocation();
			for(int i = 0; i < 2; i++) {
				if(trueSpawnLoc.clone().subtract(0, i, 0).getBlock().getType().isSolid()) {
					trueSpawnLoc = trueSpawnLoc.clone().subtract(0, i - 1, 0);
					break;
				}
			}

			Location location = trueSpawnLoc.clone().add(-1, 5, -1);

			Map<Location, BlockData> blockDataMap = SchematicPaste.getBlockMap(schematic, location);
			if(blockDataMap == null) continue;


			int height = 3;
			int ticks = Misc.getFallTime(height);

			for(Map.Entry<Location, BlockData> entry : blockDataMap.entrySet()) {
				FallingBlock fallingBlock = new FallingBlock(entry.getValue(), entry.getKey());
				fallingBlock.setViewers(getViewers());
				fallingBlock.spawnBlock();
				fallingBlock.removeAfter(ticks);
			}



			Location cageLoc = trueSpawnLoc.subtract(1, 0, 1);

			new BukkitRunnable() {
				@Override
				public void run() {

					List<PacketBlock> packetBlocks = new ArrayList<>();
					Map<Location, BlockData> blockDataMap = SchematicPaste.getBlockMap(schematic, cageLoc);

					for(Map.Entry<Location, BlockData> entry : blockDataMap.entrySet()) {
						Location location = entry.getKey();
						BlockData blockData = entry.getValue();

						PacketBlock packetBlock = new PacketBlock(blockData.material, blockData.data, location);
						packetBlock.setViewers(getViewers());
						packetBlock.spawnBlock();
						packetBlocks.add(packetBlock);
					}

					packetBlockMap.put(viewer, packetBlocks);

				}
			}.runTaskLater(PitSim.INSTANCE, ticks);

			new BukkitRunnable() {
				int runnableTicks = 0;
				@Override
				public void run() {
					Location tpLoc = cageLoc.clone().add(1, 0, 1);
					if(viewer.getLocation().distance(tpLoc) > 1.5) viewer.teleport(tpLoc);

					if(runnableTicks >= captureTicks) {
						cancel();
						return;
					}
					runnableTicks += 10;
				}
			}.runTaskTimer(PitSim.INSTANCE, 0, 10);

			new BukkitRunnable() {
				@Override
				public void run() {
					for(PacketBlock packetBlock : packetBlockMap.get(viewer)) {
						packetBlock.removeBlock();
					}
					packetBlockMap.remove(viewer);
				}
			}.runTaskLater(PitSim.INSTANCE, ticks + captureTicks);
		}
	}
}
