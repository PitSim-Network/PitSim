package dev.kyro.pitsim.controllers;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.HelmetManager;
import dev.kyro.pitsim.controllers.objects.Non;
import dev.kyro.pitsim.events.KillEvent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class BlobManager implements Listener {
	public static Map<UUID, Slime> blobMap = new HashMap<>();

	@EventHandler(priority = EventPriority.LOW)
	public void onBlobDamagePlayer(EntityDamageByEntityEvent event) {
		if(event.getEntity() instanceof Player) {
			if(NonManager.getNon((Player) event.getEntity()) != null) event.setCancelled(true);
		}
		if(!(event.getDamager() instanceof Slime) || !SpawnManager.isInSpawn(event.getEntity().getLocation())) return;
		event.setCancelled(true);
	}

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
//				TODO: Make the blob focus mid
				for(Map.Entry<UUID, Slime> entry : blobMap.entrySet()) {
					double damage = (entry.getValue().getSize() - 1);
					entry.getValue().setHealth(entry.getValue().getMaxHealth());
					for(Entity entity : entry.getValue().getNearbyEntities(0, 0, 0)) {

						if(!(entity instanceof Player)) continue;
						Non non = NonManager.getNon((Player) entity);
						if(non == null || DamageManager.nonHitCooldownList.contains(non.non)) continue;

						DamageManager.createIndirectAttack(entry.getValue(), non.non, damage);
					}
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 1L);

		new BukkitRunnable() {
			@Override
			public void run() {

				List<Entity> entities = new ArrayList<>();
				for(World world : Bukkit.getWorlds()) {
					entities.addAll(world.getEntities());
				}

				for(Entity slime : entities) {
					if(!(slime instanceof Slime)) continue;
					if(slime instanceof MagmaCube) continue;

					if(!blobMap.containsValue(slime)) slime.remove();
				}

				for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
					if(HelmetManager.toggledPlayers.contains(onlinePlayer) && HelmetManager.abilities.get(onlinePlayer).refName.equals("pitblob")) {
						if(!blobMap.containsKey(onlinePlayer.getUniqueId())) {
							HelmetManager.deactivate(onlinePlayer);

							HelmetManager.toggledPlayers.remove(onlinePlayer);
						}
					}
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 20L);
	}

	public static Player getOwner(Slime slime) {

		for(Map.Entry<UUID, Slime> entry : BlobManager.blobMap.entrySet()) {
			if(!entry.getValue().equals(slime)) continue;
			return Bukkit.getPlayer(entry.getKey());
		}
		return null;
	}

//	/*@EventHandler
//	public void onLeave(PlayerQuitEvent event) {
//		blobMap.remove(event.getPlayer().getUniqueId());
//	}*/

	@EventHandler(ignoreCancelled = true)
	public void onAttack(EntityDamageEvent event) {

		if(!(event.getEntity() instanceof Slime)) {
			return;
		}

		if(event.getEntity() instanceof MagmaCube) return;

		if(!blobMap.containsValue((Slime) event.getEntity())) {
			return;
		}

		Slime slime = (Slime) event.getEntity();

		if(getOwner(slime) == event.getEntity()) {

			event.setCancelled(true);
			return;
		}

		if(event.getFinalDamage() < slime.getHealth()) return;
		for(Map.Entry<UUID, Slime> entry : blobMap.entrySet()) {
			if(entry.getValue() != slime) continue;
			blobMap.remove(entry.getKey());
			return;
		}
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(!killEvent.isKillerPlayer() || !blobMap.containsKey(killEvent.getKiller().getUniqueId())) return;
		Slime slime = blobMap.get(killEvent.getKiller().getUniqueId());
		if(slime != null) {
			boolean isMaxSize = slime.getSize() >= getMaxSlimeSize();
			if(Math.random() < 0.25 && !isMaxSize) slime.setSize(slime.getSize() + 1);
			if(!isMaxSize) slime.setHealth(slime.getMaxHealth());
			return;
		}

		slime = (Slime) killEvent.getKiller().getWorld().spawnEntity(killEvent.getKiller().getLocation(), EntityType.SLIME);
		slime.setSize(1);
		blobMap.put(killEvent.getKiller().getUniqueId(), slime);
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if(event.getEntity() instanceof Slime && !(event.getEntity() instanceof MagmaCube)) {
//			swap to enable blob fishing (fishing rod causes errors though)
//			event.setDamage(0);
//			if(SpawnManager.isInSpawn(event.getEntity().getLocation())) event.setCancelled(true);
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		if(!(event.getDamager() instanceof Slime)) return;
		if(event.getDamager() instanceof MagmaCube) return;
		Player player = getOwner((Slime) event.getDamager());
		if(event.getEntity() == player) event.setCancelled(true);
	}

	public static int getMaxSlimeSize() {
		return 6;
	}
}
