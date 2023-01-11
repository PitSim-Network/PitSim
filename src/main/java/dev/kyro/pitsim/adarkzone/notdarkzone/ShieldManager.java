package dev.kyro.pitsim.adarkzone.notdarkzone;

import dev.kyro.pitsim.PitSim;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public class ShieldManager implements Listener {

	static {
		new BukkitRunnable() {
			int count = 0;
			@Override
			public void run() {

				if(count % 20 == 0) {
//					TODO: While active shield regeneration
				}

//				TODO: While inactive shield reset

				count++;
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 1L);
	}
}
