package dev.kyro.pitsim.tutorial;

import dev.kyro.pitsim.controllers.MapManager;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public enum TutorialObjective {
	PERKS("perks", "&ePerks and Killstreaks", 2.3, 0, PitsimTutorial.class),
	KITS("kits", "&eKits", 1.3, 0, PitsimTutorial.class),
	PRESTIGE("prestige", "&ePrestige and Renown", 2.3, 0, PitsimTutorial.class),
	KEEPER("keeper", "&2The Keeper", 2.2, 0, PitsimTutorial.class);

	public String refName;
	public String display;
	public double particleDisplayHeight;
	public int order;
	public Class<? extends Tutorial> tutorial;

	TutorialObjective(String refName, String display, double particleDisplayHeight, int order, Class<? extends Tutorial> tutorial) {
		this.refName = refName;
		this.display = display;
		this.particleDisplayHeight = particleDisplayHeight;
		this.order = order;
		this.tutorial = tutorial;
	}

	public static TutorialObjective getObjective(int order, Class<? extends Tutorial> tutorial) {
		for(TutorialObjective value : values()) {
			if(value.order == order && value.tutorial == tutorial) return value;
		}
		return null;
	}

	public static List<TutorialObjective> getObjectives(Class<? extends Tutorial> tutorial) {
		List<TutorialObjective> list = new ArrayList<>();
		for(TutorialObjective value : values()) {
			if(value.tutorial == tutorial) list.add(value);
		}
		return list;
	}

	public static int getSize(Class<? extends Tutorial> tutorial) {
		int size = 0;
		for(TutorialObjective value : values()) {
			if(value.tutorial == tutorial) size++;
		}
		return size;
	}

	public static TutorialObjective getByRefName(String refName) {
		for(TutorialObjective value : values()) {
			if(value.refName.equals(refName)) return value;
		}
		return null;
	}

	public Location getParticleLocation(World world) {
		if(this == PERKS) return MapManager.currentMap.getPerksNPCSpawn(world);
		else if(this == KITS) return MapManager.currentMap.getKitNPCSpawn(world);
		else if(this == PRESTIGE) return MapManager.currentMap.getPrestigeNPCSpawn(world);
		else if(this == KEEPER) return MapManager.currentMap.getKeeperNPCSpawn(world);
		return null;
	}
}
