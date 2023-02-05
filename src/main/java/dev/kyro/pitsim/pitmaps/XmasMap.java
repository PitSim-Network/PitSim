package dev.kyro.pitsim.pitmaps;

import com.xxmicloxx.NoteBlockAPI.model.Playlist;
import com.xxmicloxx.NoteBlockAPI.model.RepeatMode;
import com.xxmicloxx.NoteBlockAPI.songplayer.PositionSongPlayer;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import dev.kyro.pitsim.controllers.BoosterManager;
import dev.kyro.pitsim.controllers.objects.Booster;
import dev.kyro.pitsim.controllers.objects.PitMap;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.UUID;

public class XmasMap extends PitMap {

	public static PositionSongPlayer radio;

	public XmasMap(String worldName, int rotationDays) {
		super(worldName, rotationDays);

		File exampleSong = new File("plugins/NoteBlockAPI/Xmas/Frosty the Snowman.nbs");
		File dir = new File(exampleSong.getAbsoluteFile().getParent());
		File[] files = dir.listFiles();
		assert files != null;
		Playlist playlist = new Playlist(NBSDecoder.parse(files[0]));

		for(int i = 1; i < files.length; i++) {
			playlist.add(NBSDecoder.parse(files[i]));
		}

		PositionSongPlayer esp = new PositionSongPlayer(playlist);
		esp.setDistance(18);
		esp.setRepeatMode(RepeatMode.ALL);
		esp.setTargetLocation(getMid().add(0, 20, 0));
		esp.setPlaying(true);
		radio = esp;
	}

	public static Location mid = new Location(null, 0.5, 70, 0.5);

	@Override
	public Location getSpawn() {
		return new Location(world, 0.5, 88, 8.5, -180, 0);
	}

	@Override
	public Location getFromDarkzoneSpawn() {
		return new Location(world, -56, 73, 0.5, -90, 0);
	}

	@Override
	public Location getNonSpawn() {
		Location spawn = new Location(world, 0.5, 86, 0.5);
		spawn.setX(spawn.getX() + (Math.random() * 6 - 3));
		spawn.setZ(spawn.getZ() + (Math.random() * 6 - 3));

		Booster booster = BoosterManager.getBooster("chaos");
		if(booster.isActive()) {
			spawn.add(0, -10, 0);
		} else if(Math.random() < 0.5) {
			spawn.add(0, -5, 0);
		}
		return spawn;
	}

	public static void addToRadio(Player player) {
		if(radio != null) radio.addPlayer(player);
	}

	public static void removeFromRadio(Player player) {
		if(radio != null) radio.removePlayer(player);
	}

	public static boolean isListening(UUID uuid) {
		if(radio == null) return false;
		return radio.getPlayerUUIDs().contains(uuid);
	}

	@Override
	public int getTeleportAdd() {return 3;}

	@Override
	public int getTeleportY() {return 72;}

	@Override
	public String getOpenSchematic() {return "plugins/WorldEdit/schematics/doorOpen.schematic";}

	@Override
	public String getClosedSchematic() {return "plugins/WorldEdit/schematics/doorClosed.schematic";}

	@Override
	public Location getSchematicPaste() {return new Location(world, -67, 72, 3);}

	@Override
	public Location getMid() {
		Location location = mid.clone();
		location.setWorld(world);
		return location;
	}

	@Override
	public Location getPerksNPCSpawn() {
		return new Location(world, 10.5, 88, 3.5, 90, 0);
	}

	@Override
	public Location getPassNPCSpawn() {
		return new Location(world, 10.5, 88, 5.5, 90, 0);
	}

	@Override
	public Location getPrestigeNPCSpawn() {
		return new Location(world, -12.5, 88, -1.5, -90, 0);
	}

	@Override
	public Location getKyroNPCSpawn() {
		return new Location(world, 7.5, 92, -8.5, 22.5F, 11);
	}

	@Override
	public Location getWijiNPCSpawn() {
		return new Location(world, 0.5, 92, -11.5, 31, 10);
	}

	@Override
	public Location getSplkNPCSpawn() {
		return new Location(world, 8.5, 90, -7.5, 45, 0);
	}

	@Override
	public Location getStatsNPCSpawn() {
		return new Location(world, 2.5, 88, -8.5, 10, 0);
	}

	@Override
	public Location getKeeperNPCSpawn() {
		return new Location(world, -2.5, 88, -10, 10, 0);
	}

	@Override
	public Location getKitsNPCSpawn() {
		return new Location(world, -2.5, 90, 12.5, -145, 15);
	}

	@Override
	public Location getStandAlonePortalRespawn() {
		return new Location(null, -56, 73, 0.5, -90, 0);
	}
}
