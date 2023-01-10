package dev.kyro.pitsim.adarkzone.abilities;

import dev.kyro.pitsim.adarkzone.PitBossAbility;
import dev.kyro.pitsim.events.AttackEvent;
import org.bukkit.event.EventHandler;

public class TrueDamageAbility extends PitBossAbility {
	public int damage;

	public TrueDamageAbility(int damage) {
		super();
		this.damage = damage;
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!isAssignedBoss(attackEvent.getAttacker())) return;
		attackEvent.trueDamage += damage;
	}
}
