package net.pitsim.spigot.perks;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import net.pitsim.spigot.PitSim;
import net.pitsim.spigot.controllers.objects.PitPerk;
import net.pitsim.spigot.events.AttackEvent;
import net.pitsim.spigot.events.KillEvent;
import net.pitsim.spigot.misc.PitLoreBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StrengthChaining extends PitPerk {
	public static StrengthChaining INSTANCE;

	public static Map<UUID, Integer> amplifierMap = new HashMap<>();
	public static Map<UUID, Integer> durationMap = new HashMap<>();

	public StrengthChaining() {
		super("Strength-Chaining", "strength");
		INSTANCE = this;
	}

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				for(Player player : Bukkit.getOnlinePlayers()) {

					durationMap.putIfAbsent(player.getUniqueId(), 0);
					int ticksLeft = durationMap.get(player.getUniqueId());
					durationMap.put(player.getUniqueId(), Math.max(ticksLeft - 1, 0));

					if(ticksLeft == 0) amplifierMap.remove(player.getUniqueId());
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 1L);
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(!hasPerk(killEvent.getKiller()) || !killEvent.isDeadPlayer()) return;

		amplifierMap.putIfAbsent(killEvent.getKiller().getUniqueId(), 0);
		int level = amplifierMap.get(killEvent.getKiller().getUniqueId());
		amplifierMap.put(killEvent.getKiller().getUniqueId(), Math.min(level + 1, 5));
		durationMap.put(killEvent.getKiller().getUniqueId(), 140);
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!hasPerk(attackEvent.getAttacker())) return;

		amplifierMap.putIfAbsent(attackEvent.getAttacker().getUniqueId(), 0);
		attackEvent.increasePercent += amplifierMap.get(attackEvent.getAttacker().getUniqueId()) * 8;
	}

	@Override
	public ItemStack getBaseDisplayStack() {
		return new AItemStackBuilder(Material.REDSTONE)
				.getItemStack();
	}

	@Override
	public void addBaseDescription(PitLoreBuilder loreBuilder, Player player) {
		loreBuilder.addLongLine(
				"&c+8% damage &7for 7s stacking on player or bot kill"
		);
	}

	@Override
	public String getSummary() {
		return "&aStrength-Chaining &7is a perk that gives you &c+8% damage&7 after every player and bot kill " +
				"for 7 seconds, capping at 5 kills";
	}
}
