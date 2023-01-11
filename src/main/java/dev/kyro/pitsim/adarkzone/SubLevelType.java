package dev.kyro.pitsim.adarkzone;

public enum SubLevelType {
	ZOMBIE("zombie"),
	SKELETON("skeleton"),
	SPIDER("spider"),
	CREEPER("creeper"),
	BLAZE("blaze"),
	MAGMA_CUBE("magmacube"),
	ZOMBIE_PIGMAN("zombiepigman"),
	IRON_GOLEM("irongolem"),
	ENDERMAN("enderman"),
	;

	public final String identifer;

	SubLevelType(String identifer) {
		this.identifer = identifer;
	}

	public SubLevel getSubLevel() {
		for(SubLevel subLevel : DarkzoneManager.subLevels) if(subLevel.subLevelType == this) return subLevel;
		throw new RuntimeException();
	}
}
