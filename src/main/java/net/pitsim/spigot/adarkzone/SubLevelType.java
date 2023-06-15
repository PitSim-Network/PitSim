package net.pitsim.spigot.adarkzone;

public enum SubLevelType {
	ZOMBIE("zombie"),
	SKELETON("skeleton"),
	SPIDER("spider"),
	WOLF("wolf"),
	BLAZE("blaze"),
	ZOMBIE_PIGMAN("zombiepigman"),
	WITHER_SKELETON("witherskeleton"),
	CREEPER("creeper"),
	IRON_GOLEM("irongolem"),
	ENDERMAN("enderman");

	public final String identifer;

	SubLevelType(String identifer) {
		this.identifer = identifer;
	}

	public SubLevel getSubLevel() {
		for(SubLevel subLevel : DarkzoneManager.subLevels) if(subLevel.subLevelType == this) return subLevel;
		throw new RuntimeException();
	}

	public int getIndex() {
		for(int i = 0; i < values().length; i++) if(values()[i] == this) return i;
		throw new RuntimeException();
	}
}
