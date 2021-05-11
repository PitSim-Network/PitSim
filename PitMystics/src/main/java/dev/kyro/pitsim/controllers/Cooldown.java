package dev.kyro.pitsim.controllers;

public class Cooldown {

	public int initialTime;
	public int ticksLeft;
	public boolean enabled = true;

	public Cooldown(int time) {
		this.initialTime = time;
		this.ticksLeft = time;
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

		if(ticksLeft > 0) ticksLeft--; else return true;
		return false;
	}

	public void reset() {

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
}
