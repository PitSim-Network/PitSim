package dev.kyro.pitsim.adarkzone.abilities.minion;

import dev.kyro.pitsim.adarkzone.SubLevelType;

public class IronGolemMinionAbility extends MinionAbility {

	public IronGolemMinionAbility() {
		super(0, SubLevelType.IRON_GOLEM, 1);
	}

	@Override
	public void onEnable() {
		spawnMobs(null, 1);
	}
}
