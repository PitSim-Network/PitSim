package dev.kyro.pitsim.tutorial;

public enum TutorialObjective {
	PERKS("", ""),
	KITS("", ""),
	PRESTIGE("", ""),
	KEEPER("", "");

	public String refName;
	public String display;

	TutorialObjective(String refName, String displayName) {
		this.refName = refName;
		this.display = displayName;
	}

	public static TutorialObjective getByRefName(String refName) {
		for(TutorialObjective value : values()) {
			if(value.refName.equals(refName)) return value;
		}
		return null;
	}

	public static int getIndex(TutorialObjective objective) {
		for(int i = 0; i < values().length; i++) {
			if(values()[i] == objective) return i;
		}
		return -1;
	}
}
