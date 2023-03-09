package dev.kyro.pitsim.adarkzone.abilities;

import dev.kyro.pitsim.adarkzone.SubLevelType;
import dev.kyro.pitsim.adarkzone.abilities.abilitytypes.MinionAbility;

public class ZombieMinionAbility extends MinionAbility {

	public ZombieMinionAbility(int spawnAmount, int maxMobs) {
		super(SubLevelType.ZOMBIE, spawnAmount, maxMobs);
	}



}
