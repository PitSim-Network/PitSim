package dev.kyro.pitsim.events;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class WrapperEntityDamageEvent {
	private EntityDamageByEntityEvent entityDamageByEntityEvent;
	private EntityDamageEvent entityDamageEvent;

	private Entity damager;
	private LivingEntity entity;

	public WrapperEntityDamageEvent(EntityDamageByEntityEvent event) {
		this.entityDamageByEntityEvent = event;
		this.damager = event.getDamager();
		this.entity = (LivingEntity) event.getEntity();
	}

	public WrapperEntityDamageEvent(EntityDamageEvent event) {
		this.entityDamageEvent = event;
		this.entity = (LivingEntity) event.getEntity();
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
}
