package dev.kyro.pitsim.adarkzone.abilities.abilitytypes;

import dev.kyro.pitsim.adarkzone.PitBossAbility;
import dev.kyro.pitsim.adarkzone.PitMob;
import dev.kyro.pitsim.adarkzone.SubLevel;
import dev.kyro.pitsim.adarkzone.SubLevelType;
import dev.kyro.pitsim.enums.MobStatus;
import org.bukkit.Location;

import java.lang.reflect.Constructor;

public abstract class MinionAbility extends PitBossAbility {
	public SubLevelType subLevelType;
	public int spawnAmount;
	public int maxMobs;

	public MinionAbility(SubLevelType subLevelType, int spawnAmount, int maxMobs) {
		this.subLevelType = subLevelType;
		this.spawnAmount = spawnAmount;
		this.maxMobs = maxMobs;
	}

	public void spawnMob(Location location) {
		SubLevel subLevel = subLevelType.getSubLevel();

		PitMob pitMob;
		try {
			Constructor<? extends PitMob> constructor = subLevel.getMobClass().getConstructor(Location.class, MobStatus.class);
			if(location == null) location = subLevel.getMobSpawnLocation();
			MobStatus status = MobStatus.STANDARD;
			if(!location.getChunk().isLoaded()) return;
			pitMob = constructor.newInstance(location, status);
		} catch(Exception exception) {
			exception.printStackTrace();
			return;
		}
		subLevel.mobs.add(pitMob);
	}

}
