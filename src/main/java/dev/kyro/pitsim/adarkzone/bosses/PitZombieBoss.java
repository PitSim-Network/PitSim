package dev.kyro.pitsim.adarkzone.bosses;

import dev.kyro.pitsim.adarkzone.PitBoss;
import dev.kyro.pitsim.adarkzone.SubLevel;
import dev.kyro.pitsim.adarkzone.abilities.StunAbility;
import dev.kyro.pitsim.adarkzone.abilities.TrueDamageAbility;
import dev.kyro.pitsim.adarkzone.sublevels.ZombieSubLevel;
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
	public Class<? extends SubLevel> assignSubLevel() {
		return ZombieSubLevel.class;
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
	public double getReach() {
		return 0;
	}

	@Override
	public double getReachRanged() {
		return 0;
	}
}
