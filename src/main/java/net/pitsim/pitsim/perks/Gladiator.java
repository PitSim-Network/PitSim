package net.pitsim.pitsim.perks;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import net.pitsim.pitsim.PitSim;
import net.pitsim.pitsim.controllers.objects.PitPerk;
import net.pitsim.pitsim.events.AttackEvent;
import net.pitsim.pitsim.misc.Misc;
import net.pitsim.pitsim.misc.PitLoreBuilder;
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
		super("Gladiator", "gladiator");
		INSTANCE = this;
	}

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				for(Player player : Bukkit.getOnlinePlayers()) {
					if(!INSTANCE.hasPerk(player)) continue;

					nearbyPlayerMap.putIfAbsent(player.getUniqueId(), 0);
					List<Entity> players = player.getNearbyEntities(12, 12, 12);
					players.removeIf(entity -> !(entity instanceof Player));
					int nearbyPlayers = Math.min(players.size(), 10);
					if(nearbyPlayers < 3) nearbyPlayers = 0;
					nearbyPlayerMap.put(player.getUniqueId(), nearbyPlayers);
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 9L, 40L);
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!hasPerk(attackEvent.getDefender())) return;
		attackEvent.multipliers.add(Misc.getReductionMultiplier(getReduction(attackEvent.getDefenderPlayer())));
	}

	@Override
	public ItemStack getBaseDisplayStack() {
		return new AItemStackBuilder(Material.BONE)
				.getItemStack();
	}

	@Override
	public void addBaseDescription(PitLoreBuilder loreBuilder, Player player) {
		loreBuilder.addLongLine("&7Receive &9-3% &7damage per nearby player");
		loreBuilder.addLongLine("&712 block range. 3 players minimum, 10 players maximum");
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
