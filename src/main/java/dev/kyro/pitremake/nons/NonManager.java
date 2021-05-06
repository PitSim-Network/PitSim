package dev.kyro.pitremake.nons;

import dev.kyro.pitremake.PitRemake;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class NonManager {

	public static List<Non> nons = new ArrayList<>();

	public static void init() {

		new BukkitRunnable() {
			@Override
			public void run() {
				for(Non non : nons) {
					tick(non);
				}
			}
		}.runTaskTimer(PitRemake.INSTANCE, 0L, 3L);
	}

	public static void tick(Non non) {


	}
}
