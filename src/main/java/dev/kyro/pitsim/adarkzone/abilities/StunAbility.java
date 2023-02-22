package dev.kyro.pitsim.adarkzone.abilities;

import dev.kyro.pitsim.adarkzone.RoutinePitBossAbility;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class StunAbility extends RoutinePitBossAbility {

	public int duration;

	public StunAbility(double routineWeight, int duration) {

		super(routineWeight);
		this.duration = duration;
	}

	@Override
	public void onRoutineExecute() {

		double range = pitBoss.getReach();
		for(Entity nearbyEntity : pitBoss.boss.getNearbyEntities(range, range, range)) {
			if(!(nearbyEntity instanceof Player)) continue;
			Player player = (Player) nearbyEntity;
			Misc.stunEntity(player, duration);
		}
	}
}
