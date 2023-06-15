package net.pitsim.spigot.adarkzone.abilities;

import net.pitsim.spigot.adarkzone.PitBossAbility;
import net.pitsim.spigot.cosmetics.ParticleOffset;
import net.pitsim.spigot.cosmetics.particles.PortalParticle;
import net.pitsim.spigot.events.AttackEvent;
import net.pitsim.spigot.misc.Sounds;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.*;

public class TeleportAbility extends PitBossAbility {

	public int comboThreshold;
	public int radius;

	public TeleportAbility(int comboThreshold, int radius) {
		this.comboThreshold = comboThreshold;
		this.radius = radius;
	}

	public Map<UUID, Integer> comboMap = new HashMap<>();

	@EventHandler
	public void onHit(AttackEvent.Apply event) {
		if(event.getDefenderPlayer() != getPitBoss().getBoss()) return;
		if(!event.isAttackerPlayer()) return;

		Player player = event.getAttackerPlayer();
		comboMap.put(player.getUniqueId(), comboMap.getOrDefault(player.getUniqueId(), 0) + 1);

		if(comboMap.get(player.getUniqueId()) != comboThreshold) return;
		comboMap.remove(player.getUniqueId());
		teleport();
	}

	public void teleport() {
		Location centerLocation = getPitBoss().getSubLevel().getMiddle();
		List<Block> applicableBlocks = new ArrayList<>();

		for(int x = -1 * radius; x < radius + 1; x++) {
			zSearch:
			for(int z = -1 * radius; z < radius + 1; z++) {
				Location blockLocation = centerLocation.clone().add(x, 0, z);

				if(blockLocation.distance(centerLocation) > radius) continue;

				for(int i = -2; i < 3; i++) {
					if(blockLocation.clone().add(0, i, i).getBlock().getType() != Material.AIR
							&& blockLocation.clone().add(0, 1 + i, 0).getBlock().getType() == Material.AIR
							&& blockLocation.clone().add(0, 2 + i, 0).getBlock().getType() == Material.AIR
							&& blockLocation.clone().add(0, 3 + i, 0).getBlock().getType() == Material.AIR) {
						applicableBlocks.add(blockLocation.getBlock());
						continue zSearch;
					}
				}
			}
		}

		PortalParticle portalParticle = new PortalParticle();

		Block block = applicableBlocks.get(new Random().nextInt(applicableBlocks.size()));
		Sounds.TELEPORT.play(getPitBoss().getBoss().getLocation());

		for(Player viewer : getViewers()) {
			for(int i = 0; i < 50; i++) {
				portalParticle.display(viewer, getPitBoss().getBoss().getLocation(), new ParticleOffset(2, 2, 2 ,2 ,2 ,2));
			}
		}

		getPitBoss().getBoss().teleport(block.getLocation().add(0, 1, 0));
		Sounds.TELEPORT.play(getPitBoss().getBoss().getLocation());

		for(Player viewer : getViewers()) {
			for(int i = 0; i < 50; i++) {
				portalParticle.display(viewer, getPitBoss().getBoss().getLocation(), new ParticleOffset(2, 2, 2 ,2 ,2 ,2));
			}
		}
	}
}
