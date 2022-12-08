package dev.kyro.pitsim.controllers;

import dev.kyro.arcticapi.data.APlayer;
import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.brewing.PotionManager;
import dev.kyro.pitsim.controllers.objects.Hopper;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.controllers.objects.PluginMessage;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.concurrent.ExecutionException;

public class PortalManager implements Listener {

	@EventHandler
	public void onPortal(EntityPortalEvent event) {
		if(event.getEntity() instanceof Player) return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onPortal(PlayerPortalEvent event) {
		if(event.getCause() != PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) return;
		event.setCancelled(true);

		Player player = event.getPlayer();
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		if(pitPlayer.prestige < 5 && !player.isOp()) {
			player.setVelocity(new Vector(3, 1, 0));
			AOutput.error(event.getPlayer(), "&5&lDARKZONE &7You must be atleast prestige &eV &7to enter!");
			Sounds.NO.play(event.getPlayer());
			return;
		}

		if(CombatManager.isInCombat(player) && !player.isOp()) {
			player.setVelocity(new Vector(3, 1, 0));
			AOutput.error(event.getPlayer(), "&5&lDARKZONE &7You cannot be in combat and enter the darkzone!");
			Sounds.NO.play(event.getPlayer());
			return;
		}

		if(HopperManager.isHopper(player)) return;

		boolean hasHopper = false;
		for(Hopper hopper : HopperManager.hopperList) {
			if(hopper.target != player) continue;
			hasHopper = true;
			break;
		}
		if(hasHopper && !player.isOp()) {
			player.setVelocity(new Vector(3, 1, 0));
			AOutput.error(event.getPlayer(), "&c&lYOU WISH! &7Kill that hopper first :P");
			Sounds.NO.play(event.getPlayer());
			return;
		}

		if(PitSim.getStatus() == PitSim.ServerStatus.ALL) {
			Location playerLoc = player.getLocation();

			PotionManager.bossBars.remove(player);

			Location teleportLoc;
			if(player.getWorld() != Bukkit.getWorld("darkzone")) {
				teleportLoc = playerLoc.clone().add(235, 40, -97);
				teleportLoc.setWorld(Bukkit.getWorld("darkzone"));
				teleportLoc.setX(173);
				teleportLoc.setY(92);
				teleportLoc.setZ(-94);
			}
			else {
				World destination = MapManager.currentMap.world;
				teleportLoc = MapManager.currentMap.getStandAlonePortalRespawn();
				teleportLoc.setWorld(destination);
				teleportLoc.setY(72);
			}

			if(teleportLoc.getYaw() > 0 || teleportLoc.getYaw() < -180) teleportLoc.setYaw(-teleportLoc.getYaw());
			teleportLoc.add(3, 0, 0);

			player.teleport(teleportLoc);
			player.setVelocity(new Vector(1.5, 1, 0).multiply(0.25));

			PitPlayer.getPitPlayer(player).updateMaxHealth();
			player.setHealth(player.getMaxHealth());

			if(player.getWorld() == Bukkit.getWorld("darkzone")) {
				APlayer aPlayer = APlayerData.getPlayerData(player);
				FileConfiguration playerData = aPlayer.playerData;
				if(!playerData.contains("darkzonepreview")) {
					CutsceneManager.play(player);
					return;
				}

				Misc.sendTitle(player, "&d&k||&5&lDarkzone&d&k||", 40);
				Misc.sendSubTitle(player, "", 40);
				AOutput.send(player, "&7You have been sent to the &d&k||&5&lDarkzone&d&k||&7.");
			}
			else {
				Misc.sendTitle(player, "&a&lOverworld", 40);
				Misc.sendSubTitle(player, "", 40);
				AOutput.send(player, "&7You have been sent to the &a&lOverworld&7.");

				MusicManager.stopPlaying(player);
			}
			return;
		}

		LobbySwitchManager.setSwitchingPlayer(event.getPlayer());

		if(PitSim.getStatus().isDarkzone()) {

			BukkitRunnable runnable = new BukkitRunnable() {
				@Override
				public void run() {
					new PluginMessage().writeString("QUEUE").writeString(event.getPlayer().getName()).writeBoolean(true).send();
				}
			};

			try {
				pitPlayer.save(true, runnable);
			} catch(ExecutionException | InterruptedException e) {
				throw new RuntimeException(e);
			}
		} else {

			BukkitRunnable runnable = new BukkitRunnable() {
				@Override
				public void run() {
					new PluginMessage().writeString("QUEUE DARKZONE").writeString(event.getPlayer().getName()).send();
				}
			};

			try {
				pitPlayer.save(true, runnable);
			} catch(ExecutionException | InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@EventHandler
	public static void onTp(PlayerTeleportEvent event) {
		new BukkitRunnable() {
			@Override
			public void run() {
				if(!MapManager.inDarkzone(event.getPlayer())) MusicManager.stopPlaying(event.getPlayer());
			}
		}.runTaskLater(PitSim.INSTANCE, 10);

	}
}
