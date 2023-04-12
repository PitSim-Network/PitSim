package dev.kyro.pitsim.tutorial;

import dev.kyro.pitsim.PitSim;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class NPCCheckpoint {
		public final TutorialObjective objective;
		public final Location location;

		protected abstract void onCheckpointEngage(Tutorial tutorial);
		protected abstract void onCheckpointSatisfy(Tutorial tutorial);
		public abstract int getEngageDelay();
		public abstract int getSatisfyDelay();
		public abstract boolean canSatisfy(Tutorial tutorial);
	
		public NPCCheckpoint(TutorialObjective objective, Location location) {
			this.objective = objective;
			this.location = location;

			TutorialManager.checkpoints.add(this);
		}

		public void onEngage(Tutorial tutorial, int delay) {
			new BukkitRunnable() {
				@Override
				public void run() {
					tutorial.isInObjective = false;
				}
			}.runTaskLater(PitSim.INSTANCE, delay);
			onCheckpointEngage(tutorial);
		}

		public void onSatisfy(Tutorial tutorial, int delay) {
			onCheckpointSatisfy(tutorial);
			tutorial.completeObjective(objective, delay);
		}
	}