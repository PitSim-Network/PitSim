package dev.kyro.pitsim.events;

import dev.kyro.pitsim.controllers.AttackInfo;
import dev.kyro.pitsim.controllers.DamageManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class WrapperEntityDamageEvent {
	private EntityDamageByEntityEvent entityDamageByEntityEvent;
	private EntityDamageEvent entityDamageEvent;

	private Entity damager;
	private LivingEntity entity;

	private AttackInfo attackInfo;

	public WrapperEntityDamageEvent(EntityDamageByEntityEvent event) {
		this.entityDamageByEntityEvent = event;
		this.damager = event.getDamager();
		this.entity = (LivingEntity) event.getEntity();
		fetchAttackInfo();
	}

	public WrapperEntityDamageEvent(EntityDamageEvent event) {
		this.entityDamageEvent = event;
		this.entity = (LivingEntity) event.getEntity();
		fetchAttackInfo();
	}

	private void fetchAttackInfo() {
		if(!DamageManager.attackInfoMap.containsKey(entity)) return;
		this.attackInfo = DamageManager.attackInfoMap.remove(entity);
	}

	public EntityDamageEvent getSpigotEvent() {
		return hasAttacker() ? entityDamageByEntityEvent : entityDamageEvent;
	}

	public boolean hasAttacker() {
		return entityDamageByEntityEvent != null;
	}

	public Entity getDamager() {
		return damager;
	}

	public LivingEntity getEntity() {
		return entity;
	}

	public double getDamage() {
		return getSpigotEvent().getDamage();
	}

	public void setCancelled(boolean cancelled) {
		getSpigotEvent().setCancelled(cancelled);
	}

	public boolean isCancelled() {
		return getSpigotEvent().isCancelled();
	}

	public AttackInfo getAttackInfo() {
		return attackInfo;
	}

	public boolean hasAttackInfo() {
		return attackInfo != null;
	}
}
