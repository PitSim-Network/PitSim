package net.pitsim.pitsim.adarkzone;

import net.pitsim.pitsim.PitSim;
import net.pitsim.pitsim.controllers.CombatManager;
import net.pitsim.pitsim.controllers.MapManager;
import net.pitsim.pitsim.enums.PitEntityType;
import net.pitsim.pitsim.misc.Misc;
import net.pitsim.pitsim.misc.effects.PacketBlock;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class SpawnBlocker {

	public static final Location bottomLeftCorner = new Location(MapManager.getDarkzone(), 240, 91, -98);
	public static final Location topRightCorner = new Location(MapManager.getDarkzone(), 240, 106, -90);

	public static final Map<UUID, List<PacketBlock>> blockMap = new HashMap<>();

	public static final List<Player> blockedPlayers = new ArrayList<>();

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				List<Player> playerList = new ArrayList<>();
				for(Entity entity : MapManager.getDarkzone().getNearbyEntities(bottomLeftCorner, 20, 20, 20)) {
					if(!Misc.isEntity(entity, PitEntityType.REAL_PLAYER)) continue;
					Player player = (Player) entity;
					if(!CombatManager.isInCombat(player)) continue;
					if(player.isOp()) continue;
					playerList.add(player);

					if(!blockedPlayers.contains(player)) {
						blockedPlayers.add(player);
						setBarrier(player);
					}
				}

				List<Player> toRemove = new ArrayList<>();

				for(Player blockedPlayer : blockedPlayers) {
					if(!playerList.contains(blockedPlayer)) {
						toRemove.add(blockedPlayer);
					}
				}

				for(Player player : toRemove) {
					blockedPlayers.remove(player);
					removeBarrier(player);
					blockMap.remove(player.getUniqueId());
				}
			}


		}.runTaskTimer(PitSim.INSTANCE, 0, 10);
	}

	public static void init() {
		// Do nothing
	}


	public static void setBarrier(Player player) {

		List<PacketBlock> blocks = new ArrayList<>();

		for(int x = bottomLeftCorner.getBlockX(); x <= topRightCorner.getBlockX(); x++) {
			for(int y = bottomLeftCorner.getBlockY(); y <= topRightCorner.getBlockY(); y++) {
				for(int z = bottomLeftCorner.getBlockZ(); z <= topRightCorner.getBlockZ(); z++) {
					Location blockLoc = new Location(MapManager.getDarkzone(), x, y, z);

					PacketBlock packetBlock = new PacketBlock(Material.STAINED_GLASS, (byte) 14, blockLoc);
					packetBlock.setViewers(player);
					blocks.add(packetBlock.spawnBlock());
				}
			}
		}

		blockMap.put(player.getUniqueId(), blocks);
	}

	public static void removeBarrier(Player player) {

		List<PacketBlock> blocks = blockMap.get(player.getUniqueId());
		for(PacketBlock block : blocks) {
			block.removeBlock();
		}

	}

	public void onDisengage(Player player) {
		blockedPlayers.remove(player);
	}
}
