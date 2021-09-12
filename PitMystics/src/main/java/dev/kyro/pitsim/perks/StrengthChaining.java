package dev.kyro.pitsim.perks;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PitPerk;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.KillEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class StrengthChaining extends PitPerk {

	public static StrengthChaining INSTANCE;

	public static Map<UUID, Integer> amplifierMap = new HashMap<>();
	public static Map<UUID, Integer> timerMap = new HashMap<>();

	public StrengthChaining() {
		super("Strength-Chaining", "strength", new ItemStack(Material.REDSTONE), 12, false, "", INSTANCE);
		INSTANCE = this;
	}

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				for(Player player : Bukkit.getOnlinePlayers()) {

					timerMap.putIfAbsent(player.getUniqueId(), 0);
					int ticksLeft = timerMap.get(player.getUniqueId());
					timerMap.put(player.getUniqueId(), Math.max(ticksLeft - 1, 0));

					if(ticksLeft == 0) amplifierMap.remove(player.getUniqueId());
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 1L);
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {

		if(!playerHasUpgrade(killEvent.killer)) return;

		amplifierMap.putIfAbsent(killEvent.killer.getUniqueId(), 0);
		int level = amplifierMap.get(killEvent.killer.getUniqueId());
		amplifierMap.put(killEvent.killer.getUniqueId(), Math.min(level + 1, 5));
		timerMap.put(killEvent.killer.getUniqueId(), 160);
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {

		if(!playerHasUpgrade(attackEvent.attacker)) return;

		amplifierMap.putIfAbsent(attackEvent.attacker.getUniqueId(), 0);
		attackEvent.increasePercent += (amplifierMap.get(attackEvent.attacker.getUniqueId()) * 8) / 100D;
	}

	@Override
	public List<String> getDescription() {
		return new ALoreBuilder("&c+8% damage &7for 7s stacking", "&7on kill.").getLore();
	}
}
