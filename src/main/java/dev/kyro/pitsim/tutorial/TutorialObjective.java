package dev.kyro.pitsim.tutorial;

import dev.kyro.pitsim.controllers.MapManager;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public enum TutorialObjective {
	//Overworld
	PERKS(OverworldTutorial.class, "perks", "&ePerks and Killstreaks", new ParticleBox(2, 2, 1, 2.3)),
	KITS(OverworldTutorial.class, "kits", "&eKits",  new ParticleBox(2, 1, 1, 1.3)),
	PRESTIGE(OverworldTutorial.class, "prestige", "&ePrestige and Renown",  new ParticleBox(2, 2, 1, 2.3)),
	KEEPER(OverworldTutorial.class, "keeper", "&2The Keeper",  new ParticleBox(2, 2, 1, 2.2)),
	PASS(OverworldTutorial.class, "pass", "&3Battle Pass",  new ParticleBox(2, 2, 1, 2.3)),

	//Darkzone
	TAINTED_WELL(DarkzoneTutorial.class, "tainted", "&5Tainted Well",  new ParticleBox(2, 2, 1, 2.3)),
	PROGRESSION(DarkzoneTutorial.class, "progression", "&5Main Progression",  new ParticleBox(2, 2, 1, 2.3)),
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
		else if(this == TAINTED_WELL) return MapManager.getDarkzoneSpawn();
		else if(this == PROGRESSION) return MapManager.getDarkzoneSpawn();
		return null;
	}

	public static class ParticleBox {
		public Location location;
		public double height;
		public double width;
		public double length;

		public double yOffset;

		public ParticleBox(Location location, double height, double width, double length, double yOffset) {
			this(height, width, length, yOffset);
			this.location = location;
		}

		public ParticleBox(double height, double width, double length, double yOffset) {
			this.height = height;
			this.width = width;
			this.length = length;

			this.yOffset = yOffset;
		}
	}
}
