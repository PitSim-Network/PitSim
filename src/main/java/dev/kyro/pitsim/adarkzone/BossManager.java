package dev.kyro.pitsim.adarkzone;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class BossManager implements Listener {
	public static List<PitBoss> pitBosses = new ArrayList<>();

	public static boolean isBoss(LivingEntity entity) {
		return getPitBoss(entity) != null;
	}

	public static PitBoss getPitBoss(LivingEntity entity) {
		if(!(entity instanceof Player)) return null;
		for(PitBoss pitBoss : pitBosses) if(pitBoss.boss == entity) return pitBoss;
		return null;
	}
}
