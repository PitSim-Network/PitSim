package dev.kyro.pitsim.adarkzone.abilities;

import dev.kyro.pitsim.adarkzone.SubLevelType;
import dev.kyro.pitsim.adarkzone.abilities.abilitytypes.MinionAbility;
import dev.kyro.pitsim.events.AttackEvent;
import org.bukkit.event.EventHandler;

public class DefensiveMinionAbility extends MinionAbility {

	public int spawnAmount;
	public long cooldown;
	public long lastSpawn = 0;

	public DefensiveMinionAbility(SubLevelType type, int spawnAmount, int maxMobs, long cooldown) {
		super(type, maxMobs);

		this.spawnAmount = spawnAmount;
		this.cooldown = cooldown;
	}

	@EventHandler
	public void onHit(AttackEvent.Apply event) {
		if(event.getDefender() != getPitBoss().boss) return;
		if(lastSpawn + cooldown > System.currentTimeMillis()) return;
		lastSpawn = System.currentTimeMillis();

		spawnMobs(getPitBoss().boss.getLocation().add(0, 2, 0), spawnAmount);

	}

}
