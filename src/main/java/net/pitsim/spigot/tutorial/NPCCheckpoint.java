package net.pitsim.spigot.tutorial;

import net.pitsim.spigot.PitSim;
import net.pitsim.spigot.controllers.ItemFactory;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class NPCCheckpoint {
	public static final int PROCEED_MESSAGE_DELAY = 40;

	public final TutorialObjective objective;
	public final Location location;
	public final Midpoint[] midpoints;

	protected abstract void onCheckpointEngage(Tutorial tutorial);
	protected abstract void onCheckpointSatisfy(Tutorial tutorial);
	public abstract int getEngageDelay();
	public abstract int getSatisfyDelay();
	public abstract boolean canEngage(Tutorial tutorial);
	public abstract boolean canSatisfy(Tutorial tutorial);

	public abstract void onCheckPointDisengage(Tutorial tutorial);

	public NPCCheckpoint(TutorialObjective objective, Location location, Midpoint... midpoints) {
		this.objective = objective;
		this.location = location;
		this.midpoints = midpoints;

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

		if(displayProceedMessage(tutorial)) tutorial.delayTask(() -> {
			String proceedMessage = tutorial.getProceedMessage();
			if(proceedMessage != null) tutorial.sendMessage(proceedMessage, 0);
		}, (long) (delay + PROCEED_MESSAGE_DELAY * 1.5));

		tutorial.completeObjective(objective, (long) (delay + (displayProceedMessage(tutorial) ? PROCEED_MESSAGE_DELAY : 0)));
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

	public boolean displayProceedMessage(Tutorial tutorial) {
		if(tutorial.getProceedMessage() == null) return false;
		return tutorial.data.completedObjectives.size() < tutorial.getObjectiveSize() - 1;
	}
}