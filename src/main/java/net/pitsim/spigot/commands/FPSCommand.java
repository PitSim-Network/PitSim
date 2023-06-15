package net.pitsim.spigot.commands;

import dev.kyro.arcticapi.misc.AOutput;
import net.pitsim.spigot.PitSim;
import net.pitsim.spigot.controllers.MapManager;
import net.pitsim.spigot.controllers.NonManager;
import net.pitsim.spigot.controllers.SpawnManager;
import net.pitsim.spigot.controllers.objects.Non;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class FPSCommand implements CommandExecutor {
	public static List<Player> fpsToggledPlayers = new ArrayList<>();
	public static List<Player> fpsActivePlayers = new ArrayList<>();
	public static double nonHideRadius = 15;
	public static double playerHideRadius = 8;

	static {
		if(PitSim.status.isOverworld()) {
			new BukkitRunnable() {
				@Override
				public void run() {
					for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
						World world = onlinePlayer.getWorld();
						Location mid = MapManager.currentMap.getMid();
						double y = MapManager.currentMap.getY();

						if(!fpsToggledPlayers.contains(onlinePlayer)) continue;
						if(MapManager.currentMap.world != world) continue;
						Location playerLoc = onlinePlayer.getLocation();
						playerLoc.setY(y);
						if(world != mid.getWorld()) continue;
						boolean closeEnough = playerLoc.distance(mid) < nonHideRadius;
						if(closeEnough && !SpawnManager.isInSpawn(onlinePlayer)) {
							showMid(onlinePlayer);
						} else {
							hideMid(onlinePlayer);
						}
						for(Player onlinePlayer2 : Bukkit.getOnlinePlayers()) {
							Location loc2 = onlinePlayer2.getLocation();
							loc2.setY(y);
							if(onlinePlayer.getWorld() != world) continue;
							if((closeEnough && !SpawnManager.isInSpawn(onlinePlayer)) || loc2.getWorld() != mid.getWorld() || loc2.distance(mid) > playerHideRadius) {
								onlinePlayer.showPlayer(onlinePlayer2);
							}
						}
					}
				}
			}.runTaskTimer(PitSim.INSTANCE, 0L, 1L);
		}
	}

	public static void hideMid(Player player) {
		boolean alreadyHidden = fpsActivePlayers.contains(player);
		if(!alreadyHidden) for(Non non : NonManager.nons) player.hidePlayer(non.non);
		for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			if(onlinePlayer.getWorld() != player.getWorld()) continue;
			Location playerLoc = onlinePlayer.getLocation();
			playerLoc.setY(MapManager.currentMap.getY());
			if(onlinePlayer == player || playerLoc.distance(MapManager.currentMap.getMid()) > playerHideRadius)
				continue;
			player.hidePlayer(onlinePlayer);
		}
		if(!alreadyHidden) fpsActivePlayers.add(player);
	}

	public static void showMid(Player player) {
		boolean wasHidden = fpsActivePlayers.contains(player);
		if(wasHidden) {
			for(Non non : NonManager.nons) player.showPlayer(non.non);
			fpsActivePlayers.remove(player);
		}
	}

	public static void hideNewNon(Non non) {
		for(Player affectedPlayer : fpsActivePlayers) if(non.non != null) affectedPlayer.hidePlayer(non.non);
		new BukkitRunnable() {
			@Override
			public void run() {
				for(Player affectedPlayer : fpsActivePlayers)
					if(non.non != null) affectedPlayer.hidePlayer(non.non);
			}
		}.runTaskLater(PitSim.INSTANCE, 1L);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;

		if(fpsToggledPlayers.contains(player)) {
			for(Non non : NonManager.nons) player.showPlayer(non.non);
			for(Player onlinePlayer : Bukkit.getOnlinePlayers()) player.showPlayer(onlinePlayer);
			AOutput.send(player, "Nons now visible");
			fpsToggledPlayers.remove(player);
			fpsActivePlayers.remove(player);
			return false;
		} else {
			AOutput.send(player, "Nons now hidden");
			fpsToggledPlayers.add(player);
			return false;
		}
	}
}
