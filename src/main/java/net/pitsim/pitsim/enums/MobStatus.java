package net.pitsim.pitsim.enums;

public enum MobStatus {
	STANDARD,
	MINION;

	public boolean isMinion() {
		return this == MobStatus.MINION;
	}
}
