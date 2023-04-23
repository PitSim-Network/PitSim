package dev.kyro.pitsim.controllers;

import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.events.AttackEvent;
import org.bukkit.entity.LivingEntity;

import java.util.Map;
import java.util.function.Consumer;

public class AttackInfo {
	private final AttackType attackType;
	private final LivingEntity fakeAttacker;
	private Map<PitEnchant, Integer> overrideAttackerEnchantMap;
	private Map<PitEnchant, Integer> overrideDefenderEnchantMap;
	private final Consumer<AttackEvent.Apply> callback;

	public AttackInfo(AttackType attackType, LivingEntity fakeAttacker, Map<PitEnchant, Integer> overrideAttackerEnchantMap,
					  Map<PitEnchant, Integer> overrideDefenderEnchantMap, Consumer<AttackEvent.Apply> callback) {
		this.attackType = attackType;
		this.fakeAttacker = fakeAttacker;
		this.callback = callback;
		this.overrideAttackerEnchantMap = overrideAttackerEnchantMap;
		this.overrideDefenderEnchantMap = overrideDefenderEnchantMap;
	}

	public AttackType getAttackType() {
		return attackType;
	}

	public LivingEntity getFakeAttacker() {
		return fakeAttacker;
	}

	public Map<PitEnchant, Integer> getOverrideAttackerEnchantMap() {
		return overrideAttackerEnchantMap;
	}

	public void setOverrideAttackerEnchantMap(Map<PitEnchant, Integer> overrideAttackerEnchantMap) {
		this.overrideAttackerEnchantMap = overrideAttackerEnchantMap;
	}

	public Map<PitEnchant, Integer> getOverrideDefenderEnchantMap() {
		return overrideDefenderEnchantMap;
	}

	public void setOverrideDefenderEnchantMap(Map<PitEnchant, Integer> overrideDefenderEnchantMap) {
		this.overrideDefenderEnchantMap = overrideDefenderEnchantMap;
	}

	public boolean hasOverrideAttackerEnchantMap() {
		return overrideAttackerEnchantMap != null;
	}

	public boolean hasOverrideDefenderEnchantMap() {
		return overrideDefenderEnchantMap != null;
	}

	public Consumer<AttackEvent.Apply> getCallback() {
		return callback;
	}

	public enum AttackType {
		FAKE_INDIRECT,
		FAKE_DIRECT
	}
}
