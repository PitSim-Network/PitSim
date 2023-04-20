package dev.kyro.pitsim.tutorial;

import dev.kyro.pitsim.controllers.MapManager;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public enum TutorialObjective {
	//Overworld
	PERKS(OverworldTutorial.class, "perks", "&ePerks and Killstreaks", new ParticleBox(2, 1.5, 1.5)),
	KITS(OverworldTutorial.class, "kits", "&eKits",  new ParticleBox(1, 1, 1)),
	PRESTIGE(OverworldTutorial.class, "prestige", "&ePrestige and Renown",  new ParticleBox(2, 1.5, 1.5)),
	KEEPER(OverworldTutorial.class, "keeper", "&2The Keeper",  new ParticleBox(2, 1.5, 1.5)),
	PASS(OverworldTutorial.class, "pass", "&3Battle Pass",  new ParticleBox(2, 1.5, 1.5)),

	//Darkzone
	TAINTED_WELL(DarkzoneTutorial.class, "tainted", "&5Tainted Well",  new ParticleBox(10, 10, 10)),
	PROGRESSION(DarkzoneTutorial.class, "progression", "&5Main Progression",  new ParticleBox(7, 7, 10)),
	MARKET_SHOP(DarkzoneTutorial.class, "market-shop", "&3Market &7and &bShop",  new ParticleBox(7, 7, 10)),
	ALTAR(DarkzoneTutorial.class, "altar", "&5Tainted Altar",  new ParticleBox(10, 12, 12)),
	BREWING(DarkzoneTutorial.class, "brewing", "&dBrewing",  new ParticleBox(7, 7, 10)),
	MONSTER_CAVES(DarkzoneTutorial.class, "monster-caves", "&cMonster Caves",  new ParticleBox(15, 10, 10)),
	;

	public final Class<? extends Tutorial> tutorialClass;
	public final String refName;
	public final String display;
	private final ParticleBox particles;

	TutorialObjective(Class<? extends Tutorial> tutorialClass, String refName, String display, ParticleBox particles) {
		this.tutorialClass = tutorialClass;
		this.refName = refName;
		this.display = display;
		this.particles = particles;
	}

	public static List<TutorialObjective> getObjectives(Class<? extends Tutorial> tutorialClass) {
		List<TutorialObjective> objectives = new ArrayList<>();
		for(TutorialObjective value : values()) {
			if(value.tutorialClass.equals(tutorialClass)) objectives.add(value);
		}
		return objectives;
	}

	public static TutorialObjective getByRefName(String refName) {
		for(TutorialObjective value : values()) {
			if(value.refName.equals(refName)) return value;
		}
		return null;
	}

	public ParticleBox getParticleBox() {
		particles.location = getParticleLocation();
		return particles;
	}

	private Location getParticleLocation() {
		if(this == PERKS) return MapManager.currentMap.getPerksNPCSpawn();
		else if(this == KITS) return MapManager.currentMap.getKitsNPCSpawn();
		else if(this == PRESTIGE) return MapManager.currentMap.getPrestigeNPCSpawn();
		else if(this == KEEPER) return MapManager.currentMap.getKeeperNPCSpawn();
		else if(this == PASS) return MapManager.currentMap.getPassNPCSpawn();
		else if(this == TAINTED_WELL) return new Location(MapManager.getDarkzone(), 186.5, 91, -105.5);
		else if(this == PROGRESSION) return new Location(MapManager.getDarkzone(), 188.5, 91, -83);
		else if(this == MARKET_SHOP) return new Location(MapManager.getDarkzone(), 202.5, 91, -83);
		else if(this == ALTAR) return new Location(MapManager.getDarkzone(), 221.5, 92, -83.5);
		else if(this == BREWING) return new Location(MapManager.getDarkzone(), 222.5, 91, -104);
		else if(this == MONSTER_CAVES) return new Location(MapManager.getDarkzone(), 276.5, 91, -121.5);
		return null;
	}

	public static class ParticleBox {
		public Location location;
		public double height;
		public double width;
		public double length;

		public ParticleBox(Location location, double height, double width, double length) {
			this(height, width, length);
			this.location = location;
		}

		public ParticleBox(double height, double width, double length) {
			this.height = height;
			this.width = width;
			this.length = length;
		}
	}
}
