package net.pitsim.spigot.controllers;

import com.sk89q.worldguard.bukkit.BukkitUtil;
import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.myzelyam.api.vanish.VanishAPI;
import dev.kyro.arcticapi.misc.AOutput;
import net.pitsim.spigot.PitSim;
import net.pitsim.spigot.aitems.MysticFactory;
import net.pitsim.spigot.aitems.PitItem;
import net.pitsim.spigot.controllers.objects.PitPlayer;
import net.pitsim.spigot.enums.PitEntityType;
import net.pitsim.spigot.events.AttackEvent;
import net.pitsim.spigot.events.KillEvent;
import net.pitsim.spigot.events.PitQuitEvent;
import net.pitsim.spigot.misc.Misc;
import net.pitsim.spigot.misc.Sounds;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpawnManager implements Listener {
	public static Map<Player, Location> lastLocationMap = new HashMap<>();

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				for(World world : Bukkit.getWorlds()) {
					List<Entity> toRemove = new ArrayList<>();
					for(Entity entity : world.getEntities()) {
						if(!(entity instanceof Arrow)) continue;
						if(!SpawnManager.isInSpawn(entity.getLocation())) continue;
						toRemove.add(entity);
					}
					toRemove.forEach(Entity::remove);
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 20);
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if(NonManager.getNon(player) != null || HopperManager.getHopper(player) != null) return;
		Location location = player.getLocation();
		boolean isInSpawn = isInSpawn(location);

		if(isInSpawn) {
			if(player.isOp() || VanishAPI.isInvisible(player)) return;
			if(!lastLocationMap.containsKey(player)) return;

			PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
			if(pitPlayer.isOnMega()) {
				Location lastLocation = lastLocationMap.get(player);
				player.teleport(lastLocation);
				player.setVelocity(new Vector());
				AOutput.error(event.getPlayer(), "&c&c&lERROR!&7 You cannot enter spawn while on a Megastreak!");
			} else if(CombatManager.isInCombat(player)) {
				Location lastLocation = lastLocationMap.get(player);
				player.teleport(lastLocation);
				player.setVelocity(new Vector());
				AOutput.error(event.getPlayer(), "&c&c&lERROR!&7 You cannot enter spawn while in combat!");
			} else {
				lastLocationMap.remove(player);
			}
		} else {
			lastLocationMap.put(player, location);
		}
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(killEvent.isDeadPlayer()) lastLocationMap.remove(killEvent.getDeadPlayer());
	}

	@EventHandler
	public void onQuit(PitQuitEvent event) {
		lastLocationMap.remove(event.getPlayer());
	}

	@EventHandler
	public void onShoot(EntityShootBowEvent event) {
		if(!(event.getEntity() instanceof Player) || !(event.getProjectile() instanceof Arrow)) return;

		Player player = (Player) event.getEntity();

		if(isInSpawn(player)) {
			event.setCancelled(true);
			Sounds.NO.play(player);
		}
	}

	@EventHandler
	public void onAttack(AttackEvent.Pre attackEvent) {
		if(!isInSpawn(attackEvent.getAttackerPlayer()) && !isInSpawn(attackEvent.getDefender().getLocation())) return;
		attackEvent.setCancelled(true);
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		if(!isInSpawn(event.getItemDrop().getLocation())) return;
		ItemStack itemStack = event.getItemDrop().getItemStack();
		if(itemStack.getType() == Material.ENDER_CHEST || itemStack.getType() == Material.TRIPWIRE_HOOK) return;

		PitItem pitItem = ItemFactory.getItem(itemStack);
		if(pitItem == null || !pitItem.destroyIfDroppedInSpawn) return;
		if(MysticFactory.isImportant(itemStack)) return;

		event.getItemDrop().remove();
		Sounds.NO.play(event.getPlayer());
		AOutput.send(event.getPlayer(), "&c&lITEM DELETED!&7 Dropped in spawn area!");
	}

	public static boolean isInSpawn(Player player) {
		if(player == null) return false;
		return isInSpawn(player, player.getLocation());
	}

	public static boolean isInSpawn(Location location) {
		return isInSpawn(null, location);
	}

	private static boolean isInSpawn(Player player, Location location) {
		if(player != null && !player.isOp() && Misc.isEntity(player, PitEntityType.REAL_PLAYER) &&
				CombatManager.isInCombat(player)) return false;

		RegionContainer container = WorldGuardPlugin.inst().getRegionContainer();
		RegionManager regions = container.get(location.getWorld());
		assert regions != null;
		ApplicableRegionSet set = regions.getApplicableRegions((BukkitUtil.toVector(location)));

		for(ProtectedRegion region : set) if(region.getId().contains("spawn") || region.getId().contains("auction")) return true;
		return false;
	}

	public static void clearSpawnStreaks() {
		for(Player player : Bukkit.getOnlinePlayers()) {
			if(isInSpawn(player) && !lastLocationMap.containsKey(player)) {
				PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
				pitPlayer.endKillstreak();
			}
		}
	}

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				clearSpawnStreaks();
			}
		}.runTaskTimer(PitSim.INSTANCE, 20L, 20L);
	}
}
