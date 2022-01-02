package dev.kyro.pitsim.controllers;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerDataManager implements Listener {
	static {

		new BukkitRunnable() {
			int count = 0;
			@Override
			public void run() {
				if(count++ % 60 == 0) {
					for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
						PitPlayer pitPlayer = PitPlayer.getPitPlayer(onlinePlayer);
						pitPlayer.fullSave();
					}
				} else {
					for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
						PitPlayer pitPlayer = PitPlayer.getPitPlayer(onlinePlayer);
						pitPlayer.save();
					}
				}
			}
		}.runTaskTimer(PitSim.INSTANCE,  20L, 20);
	}
}
