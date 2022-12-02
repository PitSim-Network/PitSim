package dev.kyro.pitsim.pitmaps;

import com.xxmicloxx.NoteBlockAPI.model.Playlist;
import com.xxmicloxx.NoteBlockAPI.model.RepeatMode;
import com.xxmicloxx.NoteBlockAPI.songplayer.PositionSongPlayer;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import dev.kyro.pitsim.controllers.BoosterManager;
import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.objects.Booster;
import dev.kyro.pitsim.controllers.objects.PitMap;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class XmasMap extends PitMap {

	public static List<PositionSongPlayer> radio = new ArrayList<>();

	public XmasMap(String... worldNames) {
		super(worldNames);

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
		esp.setTargetLocation(getMid(lobbies.get(0)).add(0, 20, 0));
		esp.setPlaying(true);
		radio.add(esp);

		PositionSongPlayer esp2 = new PositionSongPlayer(playlist);
		esp2.setDistance(18);
		esp2.setRepeatMode(RepeatMode.ALL);
		esp2.setTargetLocation(getMid(lobbies.get(1)).add(0, 20, 0));
		esp2.setPlaying(true);
		radio.add(esp2);
	}

	public static Location mid = new Location(null, 0.5, 70, 0.5);

	@Override
	public Location getSpawn(World world) {
		if(!lobbies.contains(world) || (!MapManager.multiLobbies && world != MapManager.currentMap.firstLobby))
			return getSpawn(lobbies.get(0));
		return new Location(world, 0.5, 88, 8.5, -180, 0);
	}

	@Override
	public Location getNonSpawn(World world) {
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
		for(PositionSongPlayer positionSongPlayer : radio) {
			positionSongPlayer.addPlayer(player);
		}
	}

	public static void removeFromRadio(Player player) {
		for(PositionSongPlayer positionSongPlayer : radio) {
			positionSongPlayer.removePlayer(player);
		}
	}

	public static boolean isListening(UUID uuid) {
		for(PositionSongPlayer positionSongPlayer : radio) {
			if(positionSongPlayer.getPlayerUUIDs().contains(uuid)) return true;
		}
		return false;
	}

	@Override
	public int getTeleportAdd() { return 3; }

	@Override
	public int getTeleportY() { return 72;}

	@Override
	public String getOpenSchematic() { return "plugins/WorldEdit/schematics/doorOpen.schematic"; }

	@Override
	public String getClosedSchematic() { return "plugins/WorldEdit/schematics/doorClosed.schematic"; }

	@Override
	public Location getSchematicPaste(World world) { return new Location(world, -67, 72, 3); }

	@Override
	public Location getMid(World world) {
		Location location = mid.clone();
		location.setWorld(world);
		return location;
	}

	@Override
	public Location getPerksNPCSpawn(World world) {
		return new Location(world, 10.5, 88, 4.5, 90, 0);
	}

	@Override
	public Location getPrestigeNPCSpawn(World world) {
		return new Location(world, -12.5, 88, -1.5, -90, 0);
	}

	@Override
	public Location getKyroNPCSpawn(World world) {
		return new Location(world, 7.5, 92, -8.5, 22.5F, 11);
	}

	@Override
	public Location getWijiNPCSpawn(World world) {
		return new Location(world, 0.5, 92, -11.5, 31, 10);
	}

	@Override
	public Location getSplkNPCSpawn(World world) {
		return new Location(world, 8.5, 90, -7.5, 45, 0);
	}

	@Override
	public Location getVnxNPCSpawn(World world) {
		return new Location(world, 2.5, 88, -8.5, 10, 0);
	}

	@Override
	public Location getKeeperNPCSpawn(World world) {
		return new Location(world, -2.5, 88, -10, 10, 0);
	}

	@Override
	public Location getKitNPCSpawn(World world) {
		return new Location(world, -2.5, 90, 12.5, -145, 15);
	}

	@Override
	public Location getPortalRespawn(World world) {
		return new Location(world, -56, 73, 0.5, -90, 0);
	}
}
