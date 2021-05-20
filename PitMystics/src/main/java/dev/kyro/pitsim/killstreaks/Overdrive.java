package dev.kyro.pitsim.killstreaks;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.PitPlayer;
import dev.kyro.pitsim.controllers.killstreaks.Megastreak;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Sound;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Arrays;
import java.util.List;

public class Overdrive extends Megastreak {

	public BukkitTask runnable;

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
		return 20;
	}

	public Overdrive(PitPlayer pitPlayer) {
		super(pitPlayer);
	}

	@Override
	public void proc() {

		pitPlayer.player.getWorld().playSound(pitPlayer.player.getLocation(), Sound.WITHER_SPAWN, 1000, 1);
		runnable = new BukkitRunnable() {
			@Override
			public void run() {
				Misc.applyPotionEffect(pitPlayer.player, PotionEffectType.SPEED, 200, 0, true, false);
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 60L);
	}

	@Override
	public void reset() {

		if(runnable != null) runnable.cancel();
	}

	@Override
	public void kill() {

		if(!isOnMega()) return;
	}
}
