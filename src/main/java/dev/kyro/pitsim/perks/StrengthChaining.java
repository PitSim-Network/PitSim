package dev.kyro.pitsim.perks;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.MapManager;
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
		super("Strength-Chaining", "strength", new ItemStack(Material.REDSTONE), 12, false, "", INSTANCE, false);
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
		if(!playerHasUpgrade(killEvent.getKiller())) return;
		if(!(killEvent.getDead() instanceof Player)) return;
		if(MapManager.inDarkzone(killEvent.getKiller())) return;

		amplifierMap.putIfAbsent(killEvent.getKiller().getUniqueId(), 0);
		int level = amplifierMap.get(killEvent.getKiller().getUniqueId());
		amplifierMap.put(killEvent.getKiller().getUniqueId(), Math.min(level + 1, 5));
		timerMap.put(killEvent.getKiller().getUniqueId(), 160);
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {

		if(!playerHasUpgrade(attackEvent.getAttacker())) return;

		amplifierMap.putIfAbsent(attackEvent.getAttacker().getUniqueId(), 0);
		attackEvent.increasePercent += (amplifierMap.get(attackEvent.getAttacker().getUniqueId()) * 8) / 100D;
	}

	@Override
	public List<String> getDescription() {
		return new ALoreBuilder("&c+8% damage &7for 7s stacking", "&7on player/bot kill.").getLore();
	}
}
