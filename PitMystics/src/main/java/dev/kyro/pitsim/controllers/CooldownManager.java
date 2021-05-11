package dev.kyro.pitsim.controllers;

import dev.kyro.pitsim.PitSim;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class CooldownManager {

	public static List<Cooldown> cooldownList = new ArrayList<>();

	public static void init() {

		new BukkitRunnable() {
			@Override
			public void run() {
				List<Cooldown> toRemove = new ArrayList<>();
				for(Cooldown cooldownCooldown : cooldownList) {
					boolean shouldRemove = cooldownCooldown.tick();
					if(shouldRemove) toRemove.add(cooldownCooldown);
				}
				cooldownList.removeAll(toRemove);
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 1L);
	}

	public static void add(Cooldown cooldownCooldown) {

		if(!cooldownList.contains(cooldownCooldown)) cooldownList.add(cooldownCooldown);
	}

	public static void remove(Cooldown cooldownCooldown) {

		cooldownList.remove(cooldownCooldown);
	}
}
