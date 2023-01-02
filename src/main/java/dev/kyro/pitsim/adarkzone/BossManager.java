package dev.kyro.pitsim.adarkzone;

import dev.kyro.pitsim.PitSim;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class BossManager implements Listener {
	public static List<PitBoss> pitBosses = new ArrayList<>();

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				for(PitBoss pitBoss : pitBosses) {
					if(pitBoss.lastRoutineExecuteTick + pitBoss.routineAbilityCooldownTicks > PitSim.currentTick) return;
					if(pitBoss.skipRoutineChance != 0 && Math.random() * 100 < pitBoss.skipRoutineChance) return;
					pitBoss.lastRoutineExecuteTick = PitSim.currentTick;

					PitBossAbility routineAbility = pitBoss.getRoutineAbility();
					routineAbility.onRoutineExecute();
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 20);
	}

	public static boolean isBoss(LivingEntity entity) {
		return getPitBoss(entity) != null;
	}

	public static PitBoss getPitBoss(LivingEntity entity) {
		if(!(entity instanceof Player)) return null;
		for(PitBoss pitBoss : pitBosses) if(pitBoss.boss == entity) return pitBoss;
		return null;
	}
}
