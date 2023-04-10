package dev.kyro.pitsim.tutorial;

import org.bukkit.Location;

public abstract class NPCCheckpoint {
		public final TutorialObjective objective;
		public final Location location;
		public final int walkTime;

		public abstract void onCheckpointEngage(Tutorial tutorial);
		public abstract void onCheckpointSatisfy(Tutorial tutorial);
	
		public NPCCheckpoint(TutorialObjective objective, Location location, int walkTime) {
			this.objective = objective;
			this.location = location;
			this.walkTime = walkTime;

			TutorialManager.checkpoints.add(this);
		}

		public void onEngage(Tutorial tutorial) {
			tutorial.isInObjective = true;
			onCheckpointEngage(tutorial);
		}

		public void onSatisfy(Tutorial tutorial, int delay) {
			onCheckpointSatisfy(tutorial);
			tutorial.completeObjective(objective, delay);
		}
	}