package dev.kyro.pitsim.adarkzone.abilities;

import dev.kyro.pitsim.adarkzone.PitBoss;
import dev.kyro.pitsim.adarkzone.RoutinePitBossAbility;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

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

			Misc.applyPotionEffect(player, PotionEffectType.SLOW, duration, 7, true, false);
			Misc.applyPotionEffect(player, PotionEffectType.JUMP, duration, 128, true, false);
			Misc.applyPotionEffect(player, PotionEffectType.SLOW_DIGGING, duration, 99, true, false);

			Misc.sendTitle(player, "&cSTUNNED", duration);
			Misc.sendSubTitle(player, "&eYou cannot move!", duration);

			Sounds.COMBO_STUN.play(player);
		}
	}
}
