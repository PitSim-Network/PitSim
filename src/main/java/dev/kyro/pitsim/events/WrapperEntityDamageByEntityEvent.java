package dev.kyro.pitsim.events;

import dev.kyro.pitsim.controllers.objects.PitEnchant;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Map;

public class WrapperEntityDamageByEntityEvent extends EntityDamageByEntityEvent {
	private Map<PitEnchant, Integer> overrideAttackerEnchantMap;
	private Map<PitEnchant, Integer> overrideDefenderEnchantMap;

	public WrapperEntityDamageByEntityEvent(Entity defender, Entity attacker, double damage) {
		super(defender, attacker, DamageCause.CUSTOM, damage);
	}

	public boolean hasOverrideAttackerEnchants() {
		return overrideAttackerEnchantMap != null;
	}

	public boolean hasOverrideDefenderEnchants() {
		return overrideDefenderEnchantMap != null;
	}

	public WrapperEntityDamageByEntityEvent setOverrideAttackerEnchantMap(Map<PitEnchant, Integer> overrideAttackerEnchantMap) {
		this.overrideAttackerEnchantMap = overrideAttackerEnchantMap;
		return this;
	}

	public WrapperEntityDamageByEntityEvent setOverrideDefenderEnchantMap(Map<PitEnchant, Integer> overrideDefenderEnchantMap) {
		this.overrideDefenderEnchantMap = overrideDefenderEnchantMap;
		return this;
	}

	public Map<PitEnchant, Integer> getOverrideAttackerEnchantMap() {
		return overrideAttackerEnchantMap;
	}

	public Map<PitEnchant, Integer> getOverrideDefenderEnchantMap() {
		return overrideDefenderEnchantMap;
	}
}
