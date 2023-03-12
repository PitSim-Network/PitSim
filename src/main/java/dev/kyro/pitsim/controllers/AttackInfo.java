package dev.kyro.pitsim.controllers;

import dev.kyro.pitsim.events.AttackEvent;
import org.bukkit.entity.LivingEntity;

import java.util.function.Consumer;

public class AttackInfo {
	private LivingEntity fakeAttacker;
	private Consumer<AttackEvent> callback;

	public AttackInfo(LivingEntity fakeAttacker, Consumer<AttackEvent> callback) {
		this.fakeAttacker = fakeAttacker;
		this.callback = callback;
	}

	public LivingEntity getFakeAttacker() {
		return fakeAttacker;
	}

	public Consumer<AttackEvent> getCallback() {
		return callback;
	}
}
