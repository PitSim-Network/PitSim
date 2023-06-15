package net.pitsim.pitsim.enums;

public enum KillType {
	KILL,
	DEATH,
	FAKE_KILL;

	public boolean hasAttackerAndDefender() {
		switch(this) {
			case KILL:
			case FAKE_KILL:
				return true;
		}
		return false;
	}
}
