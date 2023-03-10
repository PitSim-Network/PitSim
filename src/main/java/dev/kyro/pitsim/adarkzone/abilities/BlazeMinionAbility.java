package dev.kyro.pitsim.adarkzone.abilities;

import dev.kyro.pitsim.adarkzone.SubLevelType;
import dev.kyro.pitsim.adarkzone.abilities.abilitytypes.MinionAbility;

public class BlazeMinionAbility extends MinionAbility {

	public int spawnAmount;
	public BlazeMinionAbility(double routineWeight, int spawnAmount, int maxMobs) {
		super(routineWeight, SubLevelType.BLAZE, maxMobs);
		this.spawnAmount = spawnAmount;
	}

	@Override
	public void onRoutineExecute() {
		spawnMobs(null, spawnAmount);
	}

}
