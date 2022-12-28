package dev.kyro.pitsim.controllers;

import be.maximvdw.featherboard.api.FeatherBoardAPI;
import com.xxmicloxx.NoteBlockAPI.NoteBlockAPI;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.EntitySongPlayer;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import eu.crushedpixel.camerastudio.CameraStudio;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CutsceneManager implements Listener {

	public static Map<Player, List<BukkitTask>> cutscenePlayers = new HashMap<>();

	public static void play(Player player) {
		FeatherBoardAPI.toggle(player);
		List<BukkitTask> runnables = new ArrayList<>();
		Location originalLocation = player.getLocation();

		MusicManager.stopPlaying(player);
		File file = new File("plugins/NoteBlockAPI/Effects/oblivion.nbs");
		Song song = NBSDecoder.parse(file);
		EntitySongPlayer esp = new EntitySongPlayer(song);
		esp.setEntity(player);
		esp.setDistance(16);
		esp.addPlayer(player);
		esp.setAutoDestroy(true);
		esp.setPlaying(true);

		player.setGameMode(GameMode.SPECTATOR);

		if(player.spigot().getPing() >= 100) {
			AOutput.send(player, "&6&lHIGH PING? &7Run &f/cutscene skip &7to skip this tutorial.");
			AOutput.send(player, "&7However, be warned, this tutorial contains vital information.");
		}
		Sounds.BOOSTER_REMIND.play(player);

		List<Location> firstSequence = new ArrayList<>();
		firstSequence.add(new Location(MapManager.getDarkzone(), 175, 95, -93, -90, 0));
		firstSequence.add(new Location(MapManager.getDarkzone(), 240, 115, -96, -90, 0));

		try {
			CameraStudio.travel(player, firstSequence, CameraStudio.parseTimeString("8s"), "", "");
		} catch(ParseException e) {
			e.printStackTrace();
		}

		BukkitTask task1 = new BukkitRunnable() {
			@Override
			public void run() {
				player.setGameMode(GameMode.SPECTATOR);
			}
		}.runTaskLater(PitSim.INSTANCE, 10);

		BukkitTask task2 = new BukkitRunnable() {
			@Override
			public void run() {
				Misc.applyPotionEffect(player, PotionEffectType.BLINDNESS, 45, 100, false, false);
			}
		}.runTaskLater(PitSim.INSTANCE, 145);


		List<Location> secondSequence = new ArrayList<>();
		secondSequence.add(new Location(MapManager.getDarkzone(), 182, 132, -142, -25, 31));
		secondSequence.add(new Location(MapManager.getDarkzone(), 233, 133, -136, 33, 26));
		secondSequence.add(new Location(MapManager.getDarkzone(), 264, 131, -99, 92, 21));

		BukkitTask task3 = new BukkitRunnable() {
			@Override
			public void run() {
				try {
					CameraStudio.travel(player, secondSequence, CameraStudio.parseTimeString("15s"), "", "");
				} catch(ParseException e) {
					e.printStackTrace();
				}
			}
		}.runTaskLater(PitSim.INSTANCE, 160);

		BukkitTask task4 = new BukkitRunnable() {
			@Override
			public void run() {
				Misc.applyPotionEffect(player, PotionEffectType.BLINDNESS, 45, 100, false, false);
			}
		}.runTaskLater(PitSim.INSTANCE, 445);


		List<Location> thirdSequence = new ArrayList<>();
		thirdSequence.add(new Location(MapManager.getDarkzone(), 237, 103, 11, -123, -11));
		thirdSequence.add(new Location(MapManager.getDarkzone(), 272, 140, -9, -119, -25));
		thirdSequence.add(new Location(MapManager.getDarkzone(), 288, 175, -19, -119, -28));
		thirdSequence.add(new Location(MapManager.getDarkzone(), 296, 189, -31, -90, -4));

		BukkitTask task5 = new BukkitRunnable() {
			@Override
			public void run() {
				try {
					CameraStudio.travel(player, thirdSequence, CameraStudio.parseTimeString("15s"), "", "");
				} catch(ParseException e) {
					e.printStackTrace();
				}
			}
		}.runTaskLater(PitSim.INSTANCE, 460);

		BukkitTask task6 = new BukkitRunnable() {
			@Override
			public void run() {
				Misc.applyPotionEffect(player, PotionEffectType.BLINDNESS, 45, 100, false, false);
			}
		}.runTaskLater(PitSim.INSTANCE, 745);


		List<Location> fourthSequence = new ArrayList<>();
		fourthSequence.add(new Location(MapManager.getDarkzone(), 253, 95, -121, -90, 0));
		fourthSequence.add(new Location(MapManager.getDarkzone(), 285, 90, -120, -120, 45));
		fourthSequence.add(new Location(MapManager.getDarkzone(), 299, 79, -130, -126, 38));
		fourthSequence.add(new Location(MapManager.getDarkzone(), 324, 72, -154, -14, 18));
		fourthSequence.add(new Location(MapManager.getDarkzone(), 340, 68, -132, -68, 25));
		fourthSequence.add(new Location(MapManager.getDarkzone(), 353, 62, -126, -70, 33));
		fourthSequence.add(new Location(MapManager.getDarkzone(), 367, 57, -125, -90, 0));
		fourthSequence.add(new Location(MapManager.getDarkzone(), 440, 54, -127.5, -90, 0));

		BukkitTask task7 = new BukkitRunnable() {
			@Override
			public void run() {
				try {
					CameraStudio.travel(player, fourthSequence, CameraStudio.parseTimeString("45s"), "", "");
				} catch(ParseException e) {
					e.printStackTrace();
				}
			}
		}.runTaskLater(PitSim.INSTANCE, 760);


		BukkitTask task8 = new BukkitRunnable() {
			@Override
			public void run() {
				Misc.applyPotionEffect(player, PotionEffectType.BLINDNESS, 65, 100, false, false);
			}
		}.runTaskLater(PitSim.INSTANCE, 1635);

		List<Location> fifthSequence = new ArrayList<>();
		fifthSequence.add(new Location(MapManager.getDarkzone(), 189, 94, -92, -33, 16));
		fifthSequence.add(new Location(MapManager.getDarkzone(), 206, 93, -93, 14, 6));
		fifthSequence.add(new Location(MapManager.getDarkzone(), 214, 94, -90, 52, 10));

		BukkitTask task9 = new BukkitRunnable() {
			@Override
			public void run() {
				try {
					CameraStudio.travel(player, fifthSequence, CameraStudio.parseTimeString("8s"), "", "");
				} catch(ParseException e) {
					e.printStackTrace();
				}
			}
		}.runTaskLater(PitSim.INSTANCE, 1660);

		BukkitTask task10 = new BukkitRunnable() {
			@Override
			public void run() {
				Misc.applyPotionEffect(player, PotionEffectType.BLINDNESS, 65, 100, false, false);
			}
		}.runTaskLater(PitSim.INSTANCE, 1795);

		List<Location> sixthSequence = new ArrayList<>();
		sixthSequence.add(new Location(MapManager.getDarkzone(), 221, 94, -105, 142, 17));
		sixthSequence.add(new Location(MapManager.getDarkzone(), 198, 94, -103, -177, 15));

		BukkitTask task11 = new BukkitRunnable() {
			@Override
			public void run() {
				try {
					CameraStudio.travel(player, sixthSequence, CameraStudio.parseTimeString("8s"), "", "");
				} catch(ParseException e) {
					e.printStackTrace();
				}
			}
		}.runTaskLater(PitSim.INSTANCE, 1820);

		BukkitTask task12 = new BukkitRunnable() {
			@Override
			public void run() {
				Misc.applyPotionEffect(player, PotionEffectType.BLINDNESS, 45, 100, false, false);
			}
		}.runTaskLater(PitSim.INSTANCE, 1955);

		BukkitTask task13 = new BukkitRunnable() {
			@Override
			public void run() {
				player.teleport(originalLocation);
				player.setGameMode(GameMode.SURVIVAL);
				cutscenePlayers.remove(player);
				esp.setPlaying(false);
				FeatherBoardAPI.toggle(player);
				NoteBlockAPI.stopPlaying(player);

				PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
				if(player.isOnline()) {
					pitPlayer.darkzoneCutscene = true;
				}
			}
		}.runTaskLater(PitSim.INSTANCE, 1980);

		runnables.add(task1);
		runnables.add(task2);
		runnables.add(task3);
		runnables.add(task4);
		runnables.add(task5);
		runnables.add(task6);
		runnables.add(task7);
		runnables.add(task8);
		runnables.add(task9);
		runnables.add(task10);
		runnables.add(task11);
		runnables.add(task12);
		runnables.add(task13);

		cutscenePlayers.put(player, runnables);


		sendTitle(player, "&d&k||&5&lDarkzone&d&k||", "&7A corrupted world", 20);
		sendTitle(player, "&eEnhanced Combat", "&7Your normal mystics won't work here", 200);
		sendTitle(player, "&eMana Bar", "&7Use Mana to cast &dSPELL! &7Enchants", 320);
		sendTitle(player, "&eA New Realm", "&7There is plenty to explore", 500);
		sendTitle(player, "&eBe Wary", "&7Strange forces lurk here", 650);

		sendTitle(player, "&eEnter the Dungeon", "&7Fight monsters for unique Items", 820);
		sendTitle(player, "&eChallenge your Skill", "&7Use drops to spawn Bosses", 1060);
		sendTitle(player, "&eStart your Descent", "&7Explore 10 Unique levels and Bosses", 1300);
		sendTitle(player, "&eReclaim the Dead", "&7Kill Bosses for &fTainted Souls", 1500);

		sendTitle(player, "&eBrew Potions", "&f10,000 &7Possible potions with Mob Items", 1700);

		sendTitle(player, "&eReady to Start?", "&7Craft and Enchant your &5Tainted Items &7here", 1840);
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent event) {

		Player toRemove = null;
		if(cutscenePlayers.containsKey(event.getPlayer())) {
			cutscenePlayers.get(event.getPlayer()).forEach(BukkitTask::cancel);
			event.getPlayer().setGameMode(GameMode.SURVIVAL);
			FeatherBoardAPI.toggle(event.getPlayer());
			toRemove = event.getPlayer();
		}
		if(toRemove != null) cutscenePlayers.remove(toRemove);
	}

	public static void skip(Player player) {
		if(cutscenePlayers.containsKey(player)) {
			cutscenePlayers.get(player).forEach(BukkitTask::cancel);
			player.setGameMode(GameMode.SURVIVAL);
			player.teleport(new Location(MapManager.getDarkzone(), 176.5, 91, -93.5, -90, 0));
			FeatherBoardAPI.toggle(player);
			NoteBlockAPI.stopPlaying(player);
			cutscenePlayers.remove(player);
			PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
			if(player.isOnline()) {
				pitPlayer.darkzoneCutscene = true;
			}
		}
	}

	public static void sendTitle(Player player, String title, String subtitle, int ticks) {
		new BukkitRunnable() {
			@Override
			public void run() {
				Misc.sendTitle(player, title, 80);
				Misc.sendSubTitle(player, subtitle, 80);
			}
		}.runTaskLater(PitSim.INSTANCE, ticks);
	}
}
