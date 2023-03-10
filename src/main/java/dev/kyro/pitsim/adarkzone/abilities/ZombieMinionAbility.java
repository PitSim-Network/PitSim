package dev.kyro.pitsim.adarkzone.abilities;

import dev.kyro.pitsim.adarkzone.SubLevelType;
import dev.kyro.pitsim.adarkzone.abilities.abilitytypes.MinionAbility;

public class ZombieMinionAbility extends MinionAbility {

	public int spawnAmount;
	public ZombieMinionAbility(double routineWeight, int spawnAmount, int maxMobs) {
		super(routineWeight, SubLevelType.ZOMBIE, maxMobs);
		this.spawnAmount = spawnAmount;
	}

	@Override
	public void onRoutineExecute() {
		spawnMobs(null, spawnAmount);
	}

}
