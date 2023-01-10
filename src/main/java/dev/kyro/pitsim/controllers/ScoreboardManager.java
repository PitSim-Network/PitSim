package dev.kyro.pitsim.controllers;

import be.maximvdw.featherboard.api.FeatherBoardAPI;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScoreboardManager implements Listener {

	public static void init() {}

	;

	public static List<Player> goldScoreboardPlayers = new ArrayList<>();
	public static List<Player> soulScoreboardPlayers = new ArrayList<>();

	public static List<String> goldEnchants = Arrays.asList("moct", "gboost", "gbump");

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				for(Player player : Bukkit.getOnlinePlayers()) {
					ItemStack leggings = player.getInventory().getLeggings();

					int count = 0;
					for(String goldEnchant : goldEnchants) {
						if(Misc.isAirOrNull(leggings)) continue;
						if(EnchantManager.getEnchantsOnItem(leggings).containsKey(EnchantManager.getEnchant(goldEnchant)))
							count++;
					}

					if(count >= 2 && !goldScoreboardPlayers.contains(player) && player.getWorld() != MapManager.getDarkzone()) {
						goldScoreboardPlayers.add(player);
						FeatherBoardAPI.showScoreboard(player, "gold");
					} else if(count < 2) {
						goldScoreboardPlayers.remove(player);
						if(player.getWorld() != MapManager.getDarkzone())
							FeatherBoardAPI.showScoreboard(player, "default");
					}
				}
			}
		}.runTaskTimerAsynchronously(PitSim.INSTANCE, 20 * 5, 20);
	}

	@EventHandler
	public void onTeleport(PlayerTeleportEvent event) {
		Player player = event.getPlayer();
		if(!Bukkit.getOnlinePlayers().contains(player)) return;

		new BukkitRunnable() {
			@Override
			public void run() {
				if(!soulScoreboardPlayers.contains(player)) {
					if(player.getWorld() == MapManager.getDarkzone()) {
						goldScoreboardPlayers.remove(player);
						soulScoreboardPlayers.add(player);
						FeatherBoardAPI.showScoreboard(player, "darkzone");
					}
				} else {
					if(player.getWorld() != MapManager.getDarkzone()) {
						soulScoreboardPlayers.remove(player);
						FeatherBoardAPI.showScoreboard(player, "default");
					}
				}
			}
		}.runTaskLater(PitSim.INSTANCE, 5);
	}
}
