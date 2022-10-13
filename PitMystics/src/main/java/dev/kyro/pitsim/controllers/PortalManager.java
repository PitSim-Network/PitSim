package dev.kyro.pitsim.controllers;

import dev.kyro.arcticapi.data.APlayer;
import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.brewing.PotionManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.controllers.objects.PluginMessage;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import dev.kyro.pitsim.perks.Streaker;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import javax.persistence.Lob;
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

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(event.getPlayer());
		if(pitPlayer.prestige < 5) {
			event.getPlayer().setVelocity(new Vector(3, 1, 0));
			AOutput.error(event.getPlayer(), "&5&lDARKZONE &7You must be atleast prestige &eV &7to enter!");
			Sounds.NO.play(event.getPlayer());
			return;
		}

		LobbySwitchManager.setSwitchingPlayer(event.getPlayer());

		if(PitSim.isDarkzone()) {

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
