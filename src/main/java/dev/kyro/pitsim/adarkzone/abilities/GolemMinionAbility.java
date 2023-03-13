package dev.kyro.pitsim.adarkzone.abilities;

import dev.kyro.pitsim.adarkzone.SubLevelType;
import dev.kyro.pitsim.adarkzone.abilities.abilitytypes.MinionAbility;

public class GolemMinionAbility extends MinionAbility {

	public int spawnAmount;
	public GolemMinionAbility(double routineWeight, int spawnAmount, int maxMobs) {
		super(routineWeight, SubLevelType.IRON_GOLEM, maxMobs);
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
