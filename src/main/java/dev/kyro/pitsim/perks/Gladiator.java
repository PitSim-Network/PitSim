package dev.kyro.pitsim.perks;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.objects.PitPerk;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Gladiator extends PitPerk {
	public static Gladiator INSTANCE;
	public static Map<UUID, Integer> nearbyPlayerMap = new HashMap<>();

	public Gladiator() {
		super("Gladiator", "gladiator", new ItemStack(Material.BONE, 1, (short) 0), 13, false, "", INSTANCE, false);
		INSTANCE = this;
	}

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				for(Player player : Bukkit.getOnlinePlayers()) {
					if(!INSTANCE.playerHasUpgrade(player)) continue;

					nearbyPlayerMap.putIfAbsent(player.getUniqueId(), 0);
					List<Entity> players = player.getNearbyEntities(12, 12, 12);
					players.removeIf(entity -> !(entity instanceof Player));
					int nearbyPlayers = players.size();
					if(nearbyPlayers > 10) nearbyPlayers = 10;
					if(nearbyPlayers < 3) nearbyPlayers = 0;
					nearbyPlayerMap.put(player.getUniqueId(), nearbyPlayers);
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 9L, 40L);
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!playerHasUpgrade(attackEvent.getDefender())) return;
		if(MapManager.inDarkzone(attackEvent.getAttacker())) return;

		attackEvent.multipliers.add(Misc.getReductionMultiplier(getReduction(attackEvent.getDefenderPlayer())));
	}

	@Override
	public List<String> getDescription() {
		return new ALoreBuilder(
				"&7Receive &9-3% &7damage per",
				"&7nearby player.",
				"",
				"&712 blocks range.",
				"&7Minimum 3, max 10 players."
		).getLore();
	}

	@Override
	public String getSummary() {
		return "&aGladiator &7gives you &9-3% damage based on how many players are around you";
	}

	public static int getNearbyPlayers(Player player) {
		return nearbyPlayerMap.getOrDefault(player.getUniqueId(), 0);
	}

	public static int getReduction(Player player) {
		return 3 * getNearbyPlayers(player);
	}
}
