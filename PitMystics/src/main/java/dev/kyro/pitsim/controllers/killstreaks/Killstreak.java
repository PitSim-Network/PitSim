package dev.kyro.pitsim.controllers.killstreaks;

import java.util.Arrays;
import java.util.List;

public abstract class Killstreak {

	public String name;
	public List<String> refNames;
	public int killInterval;

	public Killstreak(String name, int killInterval, String... refNames) {
		this.name = name;
		this.killInterval = killInterval;
		this.refNames = Arrays.asList(refNames);
	}

	public abstract void proc();
	public abstract void reset();
}
