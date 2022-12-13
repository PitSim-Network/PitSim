package dev.kyro.pitsim.tutorial;

import dev.kyro.pitsim.controllers.MapManager;
import org.bukkit.Location;

public enum TutorialObjective {
	PERKS("perks", "&ePerks and Killstreaks", 2.3),
	KITS("kits", "&eKits", 1.3),
	PRESTIGE("prestige", "&ePrestige and Renown", 2.3),
	KEEPER("keeper", "&2The Keeper", 2.2);

	public String refName;
	public String display;
	public double particleDisplayHeight;

	TutorialObjective(String refName, String display, double particleDisplayHeight) {
		this.refName = refName;
		this.display = display;
		this.particleDisplayHeight = particleDisplayHeight;
	}

	public static TutorialObjective getByRefName(String refName) {
		for(TutorialObjective value : values()) {
			if(value.refName.equals(refName)) return value;
		}
		return null;
	}

	public Location getParticleLocation() {
		if(this == PERKS) return MapManager.currentMap.getPerksNPCSpawn();
		else if(this == KITS) return MapManager.currentMap.getKitsNPCSpawn();
		else if(this == PRESTIGE) return MapManager.currentMap.getPrestigeNPCSpawn();
		else if(this == KEEPER) return MapManager.currentMap.getKeeperNPCSpawn();
		return null;
	}
}
