package net.pitsim.spigot.adarkzone.abilities.minion;

import net.pitsim.spigot.adarkzone.PitBossAbility;
import net.pitsim.spigot.adarkzone.SubLevel;
import net.pitsim.spigot.adarkzone.SubLevelType;
import net.pitsim.spigot.enums.MobStatus;
import org.bukkit.Location;

public abstract class MinionAbility extends PitBossAbility {
	public SubLevelType subLevelType;
	public int maxMobs;

	public MinionAbility(SubLevelType subLevelType, int maxMobs) {
		this.subLevelType = subLevelType;
		this.maxMobs = maxMobs;
	}

	public MinionAbility(double routineWeight, SubLevelType subLevelType, int maxMobs) {
		super(routineWeight);
		this.subLevelType = subLevelType;
		this.maxMobs = maxMobs;
	}

	public void spawnMobs(Location location, int spawnAmount) {
		for(int i = 0; i < spawnAmount; i++) {
			SubLevel subLevel = subLevelType.getSubLevel();
			if(subLevel.mobs.size() >= maxMobs) return;
			subLevel.spawnMob(location, MobStatus.MINION);
		}
	}
}
