package net.pitsim.pitsim.aserverstatistics;

public enum StatisticCategory {
	OVERWORLD_PVP("Overworld PvP"),
	OVERWORLD_STREAKING("Overworld Streaking"),
	DARKZONE_VS_PLAYER("Darkzone Player vs Player"),
	DARKZONE_VS_MOB("Darkzone Player vs Mob"),
	DARKZONE_VS_BOSS("Darkzone Player vs Boss"),
	;

	private final String displayName;

	StatisticCategory(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}
}
