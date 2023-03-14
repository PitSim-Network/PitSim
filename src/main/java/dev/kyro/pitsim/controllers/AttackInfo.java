package dev.kyro.pitsim.controllers;

import dev.kyro.pitsim.events.AttackEvent;
import org.bukkit.entity.LivingEntity;

import java.util.function.Consumer;

public class AttackInfo {
	private AttackType attackType;
	private LivingEntity fakeAttacker;
	private Consumer<AttackEvent> callback;

	public AttackInfo(AttackType attackType, LivingEntity fakeAttacker, Consumer<AttackEvent> callback) {
		this.attackType = attackType;
		this.fakeAttacker = fakeAttacker;
		this.callback = callback;
	}

	public AttackType getAttackType() {
		return attackType;
	}

	public LivingEntity getFakeAttacker() {
		return fakeAttacker;
	}

	public Consumer<AttackEvent> getCallback() {
		return callback;
	}

	public enum AttackType {
		FAKE_INDIRECT,
		FAKE_DIRECT
	}
}
