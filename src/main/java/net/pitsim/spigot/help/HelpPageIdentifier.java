package net.pitsim.spigot.help;

public enum HelpPageIdentifier {
	MAIN_PAGE,
	BEST_PERKS;

	public String getIdentifier() {
		return name();
	}
}
