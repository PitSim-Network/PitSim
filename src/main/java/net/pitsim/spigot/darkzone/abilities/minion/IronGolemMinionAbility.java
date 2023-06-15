package net.pitsim.spigot.darkzone.abilities.minion;

import net.pitsim.spigot.darkzone.SubLevelType;

public class IronGolemMinionAbility extends MinionAbility {

	public IronGolemMinionAbility() {
		super(0, SubLevelType.IRON_GOLEM, 1);
	}

	@Override
	public void onEnable() {
		spawnMobs(null, 1);
	}
}
