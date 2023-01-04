package dev.kyro.pitsim.adarkzone.bosses;

import dev.kyro.pitsim.adarkzone.PitBoss;
import dev.kyro.pitsim.adarkzone.SubLevelType;
import dev.kyro.pitsim.adarkzone.abilities.StunAbility;
import dev.kyro.pitsim.adarkzone.abilities.TrueDamageAbility;
import org.bukkit.entity.Player;

public class PitZombieBoss extends PitBoss {

	public PitZombieBoss(Player summoner) {

		super(summoner);

		abilities(
				new TrueDamageAbility(10),
				new StunAbility(0.1, 5)
		);
	}

	@Override
	public SubLevelType getSubLevelType() {
		return SubLevelType.ZOMBIE;
	}

	@Override
	public String getName() {
		return "&cZombie Boss";
	}

	@Override
	public String getSkinName() {
		return "Zombie";
	}

	@Override
	public int getMaxHealth() {
		return 100;
	}

	@Override
	public double getMeleeDamage() {
		return 0;
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
