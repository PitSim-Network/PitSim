package dev.kyro.pitsim.tutorial;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.ItemFactory;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class NPCCheckpoint {
		public final TutorialObjective objective;
		public final Location location;

		protected abstract void onCheckpointEngage(Tutorial tutorial);
		protected abstract void onCheckpointSatisfy(Tutorial tutorial);
		public abstract int getEngageDelay();
		public abstract int getSatisfyDelay();
		public abstract boolean canEngage(Tutorial tutorial);
		public abstract boolean canSatisfy(Tutorial tutorial);

		public abstract void onCheckPointDisengage(Tutorial tutorial);
	
		public NPCCheckpoint(TutorialObjective objective, Location location) {
			this.objective = objective;
			this.location = location;

			TutorialManager.checkpoints.add(this);
		}

		public void onEngage(Tutorial tutorial, int delay) {
			Player player = tutorial.getPlayer();
			removeTutorialItems(player);

			player.updateInventory();

			tutorial.isInObjective = true;

			new BukkitRunnable() {
				@Override
				public void run() {
					tutorial.isInObjective = false;
				}
			}.runTaskLater(PitSim.INSTANCE, delay);
			onCheckpointEngage(tutorial);
		}

		public void onDisengage(Tutorial tutorial) {
			removeTutorialItems(tutorial.getPlayer());
			onCheckPointDisengage(tutorial);
		}

		public void onSatisfy(Tutorial tutorial, int delay) {
			onCheckpointSatisfy(tutorial);
			tutorial.completeObjective(objective, delay);
		}

		public static void removeTutorialItems(Player player) {
			for(int i = 0; i < player.getInventory().getContents().length; i++) {
				ItemStack content = player.getInventory().getContents()[i];
				if(ItemFactory.isTutorialItem(content)) player.getInventory().setItem(i, null);
			}

			for(int i = 0; i < player.getInventory().getArmorContents().length; i++) {
				ItemStack[] contents = player.getInventory().getArmorContents();
				ItemStack content = player.getInventory().getArmorContents()[i];
				if(ItemFactory.isTutorialItem(content)) contents[i] = null;
				player.getInventory().setArmorContents(contents);
			}

			player.updateInventory();
		}
	}