package net.pitsim.spigot.enums;

public enum ApplyType {

	BOWS,
	SWORDS,
	MELEE,
	PANTS,
	WEAPONS,
	ALL,
	NONE,

	CHESTPLATES,
	SCYTHES,
	TAINTED;

	public boolean isTainted() {
		return this == CHESTPLATES || this == SCYTHES || this == TAINTED;
	}
}
