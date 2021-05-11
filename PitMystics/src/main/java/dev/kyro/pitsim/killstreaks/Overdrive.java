package dev.kyro.pitsim.killstreaks;

import dev.kyro.pitsim.controllers.Megastreak;

public class Overdrive extends Megastreak {

	public Overdrive(String name, String prefix, String... refNames) {
		super("&c&lOVERDRIVE", "&c&lOVRDRV", 5);
	}

	@Override
	public void onMega() {

	}

	@Override
	public void onDeath() {

	}
}
