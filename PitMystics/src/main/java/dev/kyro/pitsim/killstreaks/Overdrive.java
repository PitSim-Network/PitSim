package dev.kyro.pitsim.killstreaks;

import dev.kyro.pitsim.controllers.PitPlayer;
import dev.kyro.pitsim.controllers.killstreaks.Megastreak;

import java.util.Arrays;
import java.util.List;

public class Overdrive extends Megastreak {

	@Override
	public String getName() {
		return "&c&lOVERDRIVE";
	}

	@Override
	public String getPrefix() {
		return "&c&lOVRDRV";
	}

	@Override
	public List<String> getRefNames() {
		return Arrays.asList("overdrive");
	}

	@Override
	public int getRequiredKills() {
		return 5;
	}

	public Overdrive(PitPlayer pitPlayer) {
		super(pitPlayer);
	}

	@Override
	public void proc() {

	}

	@Override
	public void reset() {

	}

	@Override
	public void kill() {

		if(!isOnMega()) return;
//		pitPlayer.player.getWorld().playSound(pitPlayer.player.getLocation(), Sound.WITHER_SPAWN, 1000, 1);
	}
}
