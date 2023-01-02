package dev.kyro.pitsim.adarkzone.bosses;

import dev.kyro.pitsim.adarkzone.PitBoss;
import dev.kyro.pitsim.adarkzone.abilities.StunAbility;
import dev.kyro.pitsim.adarkzone.abilities.TrueDamageAbility;
import org.bukkit.entity.Player;

public class PitZombieBoss extends PitBoss {

	public PitZombieBoss(Player summoner) {
		super(summoner);

		abilities.add(new TrueDamageAbility(10));
		routineAbilityMap.put(new StunAbility(0.1,5), 0.1);


	}

	@Override
	public int getMaxHealth() {
		return 100;
	}

	@Override
	public double getReach() {
		return 0;
	}

	@Override
	public double getReachRanged() {
		return 0;
	}
}
