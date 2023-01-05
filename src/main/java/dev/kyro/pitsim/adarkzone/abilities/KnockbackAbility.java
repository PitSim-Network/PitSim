package dev.kyro.pitsim.adarkzone.abilities;

import dev.kyro.pitsim.adarkzone.RoutinePitBossAbility;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class KnockbackAbility extends RoutinePitBossAbility {

	public int intensity;

	public KnockbackAbility(double routineWeight, int intensity) {

		super(routineWeight);
		this.intensity = intensity;
	}

	@Override
	public void onRoutineExecute() {

		double range = pitBoss.getReach();
		for(Entity nearbyEntity : pitBoss.boss.getNearbyEntities(range, range, range)) {
			if(!(nearbyEntity instanceof Player)) continue;
			Player player = (Player) nearbyEntity;
			player.setVelocity(player.getLocation().toVector().subtract(pitBoss.boss.getLocation().toVector()).normalize().multiply(intensity));
		}
	}
}
