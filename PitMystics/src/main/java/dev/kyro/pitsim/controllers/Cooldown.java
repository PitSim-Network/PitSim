package dev.kyro.pitsim.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Cooldown {

	public UUID playerUUID;
	public int initialTime;
	public int ticksLeft;
	public boolean enabled = true;
	private final List<CooldownModifier> cooldownModifiers;

	public Cooldown(UUID playerUUID, int time, CooldownModifier... cooldownModifiers) {
		this.playerUUID = playerUUID;
		this.initialTime = time;
		this.ticksLeft = time;
		this.cooldownModifiers = new ArrayList<>(Arrays.asList(cooldownModifiers));
		CooldownManager.add(this);
	}

	public Cooldown start() {

		enabled = true;
		return this;
	}

	public void pause() {

		enabled = false;
	}

	public boolean tick() {

		if(!enabled) return false;

		if(ticksLeft > 0) ticksLeft--;
		else return true;
		return false;
	}

	public void restart() {

		ticksLeft = initialTime;
		CooldownManager.add(this);
	}

	public boolean isOnCooldown() {

		return ticksLeft > 0;
	}

	public int getTicksLeft() {

		return ticksLeft;
	}

	public int reduceCooldown(int ticks) {
		for(int i = 0; i < ticks; i++) tick();

		return ticksLeft;
	}

	public List<CooldownModifier> getCooldownModifiers() {
		return cooldownModifiers;
	}

	public void addModifier(CooldownModifier cooldownModifier) {
		if(!cooldownModifiers.contains(cooldownModifier)) cooldownModifiers.add(cooldownModifier);
	}

	public boolean hasModifier(CooldownModifier modifier) {
		return cooldownModifiers.contains(modifier);
	}

	public enum CooldownModifier {
		TELEBOW
	}
}
