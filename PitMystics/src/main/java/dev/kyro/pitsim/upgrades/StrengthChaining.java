package dev.kyro.pitsim.upgrades;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.PitUpgrade;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StrengthChaining extends PitUpgrade {

	public StrengthChaining() {
		super("Vampire", new ItemStack(Material.REDSTONE), 12);
	}

	public static Map<Player, Integer> strength = new HashMap<>();
	public static Map<Player, Integer> timer = new HashMap<>();


	static {
		new BukkitRunnable() {
			@Override
			public void run() {

				for(Player player : Bukkit.getOnlinePlayers()) {


					if(!strength.containsKey(player)) return;

					if(!timer.containsKey(player)) timer.put(player, 7);

					int seconds = timer.get(player);
					timer.put(player, seconds - 1);

					if(timer.get(player).equals(0)) {
						timer.remove(player);
						strength.remove(player);
					}
				}


			}
		}.runTaskTimer(PitSim.INSTANCE, 20L, 20L);
	}


	@EventHandler
	public void onKill(KillEvent killEvent) {

		if(!playerHasUpgrade(killEvent.killer)) return;

			if(strength.containsKey(killEvent.killer)) {
				int level = strength.get(killEvent.killer);

				if(level == 5) strength.put(killEvent.killer, 5);
				else strength.put(killEvent.killer, level + 1);

				timer.put(killEvent.killer, 7);
			}


	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {

		if(!playerHasUpgrade(attackEvent.attacker)) return;

		attackEvent.increasePercent += (strength.get(attackEvent.attacker) * 8) / 100D;

	}




	@Override
	public List<String> getDescription() {
		return new ALoreBuilder("gay perk").getLore();
	}
}
