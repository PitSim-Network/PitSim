package dev.kyro.pitsim.adarkzone;

public enum SubLevelType {
	ZOMBIE,
	SKELETON,
	SPIDER,
	CREEPER,
	CAVE_SPIDER,
	MAGMA_CUBE,
	ZOMBIE_PIGMAN,
	IRON_GOLEM,
	ENDERMAN;

	public SubLevel getSubLevel() {
		for(SubLevel subLevel : DarkzoneManager.subLevels) if(subLevel.subLevelType == this) return subLevel;
		throw new RuntimeException();
	}
}
