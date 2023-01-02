package dev.kyro.pitsim.adarkzone;

public abstract class RoutinePitBossAbility extends PitBossAbility {
	private double routineWeight;

	public RoutinePitBossAbility(PitBoss pitBoss, double routineWeight) {
		super(pitBoss);
		this.routineWeight = routineWeight;
	}

	public double getRoutineWeight() {
		return routineWeight;
	}
}
