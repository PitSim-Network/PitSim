package dev.kyro.pitsim.adarkzone;

public abstract class RoutinePitBossAbility extends PitBossAbility {
	private double routineWeight;

	public RoutinePitBossAbility(double routineWeight) {
		this.routineWeight = routineWeight;
	}

	public double getRoutineWeight() {
		return routineWeight;
	}
}
