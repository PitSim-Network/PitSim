package net.pitsim.spigot.enums;

public enum MobStatus {
	STANDARD,
	MINION;

	public boolean isMinion() {
		return this == MobStatus.MINION;
	}
}
