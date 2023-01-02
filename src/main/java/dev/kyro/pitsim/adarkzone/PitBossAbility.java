package dev.kyro.pitsim.adarkzone;

import dev.kyro.pitsim.PitSim;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Listener;

public abstract class PitBossAbility implements Listener {
	public PitBoss pitBoss;

	public boolean runsOnRoutine = false;
	public double routineWeight;

	public PitBossAbility(PitBoss pitBoss) {
		this.pitBoss = pitBoss;

		Bukkit.getPluginManager().registerEvents(this, PitSim.INSTANCE);
	}

//	Internal events (override to add functionality)
	public void onRoutineExecute() {}

	public boolean shouldExecuteRoutine() {
		return true;
	}

	public PitBossAbility runOnRoutine(double weight) {
		this.runsOnRoutine = true;
		this.routineWeight = weight;
		return this;
	}

	public boolean isAssignedBoss(LivingEntity entity) {
		return pitBoss.boss == entity;
	}
}
