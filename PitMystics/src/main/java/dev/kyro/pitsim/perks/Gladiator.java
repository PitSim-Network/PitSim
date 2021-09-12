package dev.kyro.pitsim.perks;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.PitSim;
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
	public static Map<UUID, Integer> amplifierMap = new HashMap<>();

	public Gladiator() {
		super("Gladiator", "gladiator", new ItemStack(Material.BONE, 1, (short) 0), 13, false, "", INSTANCE);
		INSTANCE = this;
	}


	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				for(Player player : Bukkit.getOnlinePlayers()) {
					amplifierMap.putIfAbsent(player.getUniqueId(), 0);
					List<Entity> players = player.getNearbyEntities(12, 12, 12);
					players.removeIf(entity -> !(entity instanceof Player));
					int reduction = players.size();
					if(reduction > 10) reduction = 10;
					if(reduction < 3) reduction = 0;
					amplifierMap.put(player.getUniqueId(), reduction);

				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 20L);
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!playerHasUpgrade(attackEvent.defender)) return;

		attackEvent.multiplier.add(Misc.getReductionMultiplier(3 * amplifierMap.getOrDefault(attackEvent.defender.getUniqueId(), 0)));
	}

	@Override
	public List<String> getDescription() {
		return new ALoreBuilder("&7Receive &9-3% &7damage per", "&7nearby player.", "", "&712 blocks range.", "&7Minimum 3, max 10 players.").getLore();
	}

	public boolean hasPerk(Player player) {

		return playerHasUpgrade(player);
	}
}
