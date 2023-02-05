package dev.kyro.pitsim.adarkzone.notdarkzone;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public class ShieldManager implements Listener {
	public static final double ACTIVE_REGEN_AMOUNT = 0.02;

	static {
		new BukkitRunnable() {
			int count = 0;
			@Override
			public void run() {
				for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
					PitPlayer pitPlayer = PitPlayer.getPitPlayer(onlinePlayer);
					if(pitPlayer.shield.isActive()) {
						pitPlayer.shield.addShield(ACTIVE_REGEN_AMOUNT);
					} else {
						pitPlayer.shield.regenerateTick();
					}
					pitPlayer.updateXPBar();
				}
				count++;
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 1L);
	}
}
