package net.pitsim.spigot.darkzone.abilities;

import net.pitsim.spigot.PitSim;
import net.pitsim.spigot.darkzone.PitBossAbility;
import net.pitsim.spigot.misc.Sounds;
import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class DisorderAbility extends PitBossAbility {

	public int swaps;

	public DisorderAbility(double routineWeight, int swaps) {

		super(routineWeight);
		this.swaps = swaps;
	}

	@Override
	public void onRoutineExecute() {
		for(Player viewer : getViewers()) {
			Sounds.DISORDER2.play(viewer);

			new BukkitRunnable() {
				int i = 0;

				@Override
				public void run() {
					if(!isEnabled() || !isNearToBoss(viewer)) return;

					if(i <= 20) {
						viewer.spigot().playEffect(viewer.getLocation(), Effect.POTION_SWIRL, 0, 0, 10, 10, 10, 20, 128, 100);
						i++;
						return;
					}

					swap(viewer);

					if(i >= swaps + 20) {
						cancel();
						return;
					}
					i++;
				}
			}.runTaskTimer(PitSim.INSTANCE, 0, 2);
		}
	}

	public void swap(Player player) {
		Random random = new Random();
		int first = random.nextInt(9);
		int second = random.nextInt(9);

		ItemStack firstItem = player.getInventory().getItem(first);
		ItemStack secondItem = player.getInventory().getItem(second);
		player.getInventory().setItem(first, secondItem);
		player.getInventory().setItem(second, firstItem);
		player.updateInventory();

		Sounds.DISORDER.play(player);
	}
}
