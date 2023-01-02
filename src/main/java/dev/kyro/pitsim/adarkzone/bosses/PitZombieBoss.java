package dev.kyro.pitsim.adarkzone.bosses;

import dev.kyro.pitsim.adarkzone.PitBoss;

public class PitZombieBoss extends PitBoss {
	@Override
	public int getMaxHealth() {
		return 100;
	}

	@Override
	public int getReach() {
		return 0;
	}
}
