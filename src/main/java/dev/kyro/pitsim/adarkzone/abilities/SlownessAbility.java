package dev.kyro.pitsim.adarkzone.abilities;

import dev.kyro.pitsim.adarkzone.PitBoss;
import dev.kyro.pitsim.adarkzone.PitBossAbility;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SlownessAbility extends PitBossAbility {

	public int duration;
	public int amplifier;

	public SlownessAbility(PitBoss pitBoss, int duration, int amplifier) {

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
		pitBoss.boss.getNearbyEntities(range, range, range).stream()
				.filter(entity -> entity != null)
				.map(entity -> (Player) entity)
				.forEach(player -> player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, duration, amplifier)));




	}


}
