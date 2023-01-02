package dev.kyro.pitsim.adarkzone.abilities;

import dev.kyro.pitsim.adarkzone.PitBoss;
import dev.kyro.pitsim.adarkzone.PitBossAbility;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class StunAbility extends PitBossAbility {

	public int duration;
	public int amplifier;

	public StunAbility(PitBoss pitBoss, int duration, int amplifier) {

		super(pitBoss);
		this.duration = duration;
		this.amplifier = amplifier;
	}

	@Override
	public void onRoutineExecute() {

		//give all players in pitboss rangle slowness effect for duration and amplifier
		//find all players in range of pitboss
		//for each player, give them slowness effect

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
