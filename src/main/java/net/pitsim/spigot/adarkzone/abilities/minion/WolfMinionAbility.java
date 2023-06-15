package net.pitsim.spigot.adarkzone.abilities.minion;

import net.pitsim.spigot.PitSim;
import net.pitsim.spigot.adarkzone.SubLevel;
import net.pitsim.spigot.adarkzone.SubLevelType;
import net.pitsim.spigot.events.AttackEvent;
import net.pitsim.spigot.misc.Misc;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class WolfMinionAbility extends MinionAbility {
	public BukkitTask task;

	public int spawnAmount;
	public WolfMinionAbility(double routineWeight, int spawnAmount, int maxMobs) {
		super(routineWeight, SubLevelType.WOLF, maxMobs);
		this.spawnAmount = spawnAmount;

		SubLevel subLevel = subLevelType.getSubLevel();

		task = new BukkitRunnable() {
			@Override
			public void run() {
				for(Player viewer : getViewers()) {
					Misc.sendTitle(viewer, "", 20);
					Misc.sendSubTitle(viewer, "&9" + subLevel.mobs.size() + "%", 20);
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0, 20);
	}

	@EventHandler
	public void onHit(AttackEvent.Apply event) {
		if(event.getDefender() != getPitBoss().getBoss());
		event.multipliers.add(Misc.getReductionMultiplier(subLevelType.getSubLevel().mobs.size()));
	}

	@Override
	public void onRoutineExecute() {
		spawnMobs(null, spawnAmount);
	}

	@Override
	public void disable() {
		super.disable();
		task.cancel();
	}
}
