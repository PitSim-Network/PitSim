package dev.kyro.pitsim.adarkzone.abilities;

import dev.kyro.pitsim.adarkzone.SubLevelType;
import dev.kyro.pitsim.adarkzone.abilities.abilitytypes.MinionAbility;

public class CreeperMinionAbility extends MinionAbility {

	public int spawnAmount;
	public CreeperMinionAbility(double routineWeight, int spawnAmount, int maxMobs) {
		super(routineWeight, SubLevelType.CREEPER, maxMobs);
		this.spawnAmount = spawnAmount;
	}

	@Override
	public void onRoutineExecute() {
		spawnMobs(null, spawnAmount);
	}

	@Override
	public boolean shouldExecuteRoutine() {
		return subLevelType.getSubLevel().mobs.size() < maxMobs;
	}

}
