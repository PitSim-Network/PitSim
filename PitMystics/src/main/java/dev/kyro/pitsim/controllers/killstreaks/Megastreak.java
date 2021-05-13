package dev.kyro.pitsim.controllers.killstreaks;

import java.util.Arrays;
import java.util.List;

public abstract class Megastreak {

	public String name;
	public String prefix;
	public List<String> refNames;
	public int requiredKills;

	public Megastreak(String name, String prefix, int requiredKills, String... refNames) {
		this.name = name;
		this.prefix = prefix;
		this.requiredKills = requiredKills;
		this.refNames = Arrays.asList(refNames);
	}

	public abstract void proc();
	public abstract void reset();
}
