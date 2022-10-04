package dev.kyro.pitsim.controllers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.sk89q.worldguard.bukkit.BukkitUtil;
import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.events.OofEvent;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class SpawnManager implements Listener {
	public static Map<Player, Location> lastLocationMap = new HashMap<>();

	static {
		ProtocolLibrary.getProtocolManager().addPacketListener(
				new PacketAdapter(PitSim.INSTANCE, PacketType.Play.Server.NAMED_SOUND_EFFECT) {
					@Override
					public void onPacketSending(PacketEvent event) {
						String soundName = event.getPacket().getStrings().read(0);
						if(soundName.equals("mob.villager.idle")) {
							event.setCancelled(true);
						}
					}
				});
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if(NonManager.getNon(player) != null || HopperManager.getHopper(player) != null) return;
		Location location = player.getLocation();
		boolean isInSpawn = isInSpawn(location);

		if(isInSpawn) {
			if(player.isOp()) return;
			if(!lastLocationMap.containsKey(player)) return;

			PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
			if(	pitPlayer.megastreak.isOnMega()) {
				Location lastLocation = lastLocationMap.get(player);
				player.teleport(lastLocation);
				player.setVelocity(new Vector());
				AOutput.error(event.getPlayer(), "&c&c&lNOPE! &7You cannot enter spawn while on a Megastreak!");
			} else if(CombatManager.isInCombat(player)) {
				Location lastLocation = lastLocationMap.get(player);
				player.teleport(lastLocation);
				player.setVelocity(new Vector());
				AOutput.error(event.getPlayer(), "&c&c&lNOPE! &7You cannot enter spawn while in combat!");
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
	public void onOof(OofEvent event) {
		lastLocationMap.remove(event.getPlayer());
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		lastLocationMap.remove(event.getPlayer());
	}

	@EventHandler
	public void onShoot(EntityShootBowEvent event) {
		if(!(event.getEntity() instanceof Player)) return;
		if(!(event.getProjectile() instanceof Arrow)) return;

		Player player = (Player) event.getEntity();

		if(isInSpawn(player.getLocation()) || isInDarkzoneSpawn(player.getLocation())) {
			event.setCancelled(true);
			Sounds.NO.play(player);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onUse(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if(!isInDarkzoneSpawn(player.getLocation())) return;

		ItemStack item = player.getInventory().getItemInHand();
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
			if(item.getType() != Material.GOLD_HOE) return;
			event.setCancelled(true);
			AOutput.send(player, "&c&c&lNOPE! &7You cannot use this in the spawn area!");
		}
	}

	@EventHandler
	public void onAttack(AttackEvent.Pre event) {
		Player player = event.getAttackerPlayer();
		if(!event.isAttackerPlayer()) return;
		if(!isInDarkzoneSpawn(player.getLocation())) return;

		event.setCancelled(true);
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		if(!isInSpawn(event.getItemDrop().getLocation())) return;
		ItemStack dropped = event.getItemDrop().getItemStack();
		NBTItem nbtItem = new NBTItem(dropped);
		if(nbtItem.hasKey(NBTTag.DROP_CONFIRM.getRef())) return;
		if(dropped.getType() == Material.ENDER_CHEST || dropped.getType() == Material.TRIPWIRE_HOOK) return;
		event.getItemDrop().remove();
		Sounds.NO.play(event.getPlayer());
		AOutput.send(event.getPlayer(), "&c&lITEM DELETED! &7Dropped in spawn area.");
	}

	public static Boolean isInSpawn(Location loc) {
		RegionContainer container = WorldGuardPlugin.inst().getRegionContainer();
		RegionManager regions = container.get(loc.getWorld());
		assert regions != null;
		ApplicableRegionSet set = regions.getApplicableRegions((BukkitUtil.toVector(loc)));

		for(ProtectedRegion region : set) {
			if(region.getId().equals("spawn") || region.getId().equals("spawn2")) {
				return true;
			}
		}
		return false;
	}

	public static Boolean isInDarkzoneSpawn(Location loc) {
		RegionContainer container = WorldGuardPlugin.inst().getRegionContainer();
		RegionManager regions = container.get(loc.getWorld());
		assert regions != null;
		ApplicableRegionSet set = regions.getApplicableRegions((BukkitUtil.toVector(loc)));

		for(ProtectedRegion region : set) {
			if(region.getId().equals("darkzonespawn") || region.getId().equals("darkauction")) {
				return true;
			}
		}
		return false;
	}

	public static void clearSpawnStreaks() {
		for(Player player : Bukkit.getOnlinePlayers()) {
			if(isInSpawn(player.getLocation()) && !lastLocationMap.containsKey(player)) {
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
