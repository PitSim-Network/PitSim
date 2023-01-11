package dev.kyro.pitsim.adarkzone.notdarkzone;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public class ShieldManager implements Listener {

	static {
		new BukkitRunnable() {
			int count = 0;
			@Override
			public void run() {
				for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
					PitPlayer pitPlayer = PitPlayer.getPitPlayer(onlinePlayer);
					if(pitPlayer.shield.isActive()) {
						if(count % Shield.ACTIVE_REGEN_TICKS != 0) continue;
						pitPlayer.shield.addShield(Shield.ACTIVE_REGEN_AMOUNT);
					} else {
						pitPlayer.shield.regenerateTick();
					}
				}
				count++;
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 1L);
	}
}
