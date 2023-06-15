package net.pitsim.spigot.ahelp;

public enum HelpPageIdentifier {
	MAIN_PAGE,
	BEST_PERKS;

	public String getIdentifier() {
		return name();
	}
}
