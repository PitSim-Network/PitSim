package dev.kyro.pitsim.adarkzone.slayers.tainted;

public enum SimpleSkin {
	ZOMBIE("Zombie"),
	SKELETON("Skeleton"),
	SPIDER("1spider"),
	CREEPER("Creeper"),
	CAVE_SPIDER("Cave_Spider"),
	PIGMAN("Pigman"),
	MAGMA("Magma_Cube"),
	IRON_GOLEM("_Iron_Golem_"),
	WITHER_SKELETON("Wither_Skeleton"),
	ENDERMAN("_Enderman_");

	public String skin;

	SimpleSkin(String skin) {
		this.skin = skin;
	}
}
