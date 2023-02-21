package dev.kyro.pitsim.perks;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.objects.PitPerk;
import dev.kyro.pitsim.events.AttackEvent;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirstStrike extends PitPerk {
	public static FirstStrike INSTANCE;
	public static Map<Player, List<LivingEntity>> hitPlayers = new HashMap<>();

	public FirstStrike() {
		super("First Strike", "firststrike", new ItemStack(Material.COOKED_CHICKEN), 15, true, "FIRST_STRIKE", INSTANCE, false);
		INSTANCE = this;
	}

	@EventHandler
	public void onHit(AttackEvent.Apply attackEvent) {
		if(!playerHasUpgrade(attackEvent.getAttacker())) return;
		if(MapManager.inDarkzone(attackEvent.getAttacker())) return;

		hitPlayers.putIfAbsent(attackEvent.getAttackerPlayer(), new ArrayList<>());
		List<LivingEntity> hitList = hitPlayers.get(attackEvent.getAttackerPlayer());

		if(hitList.contains(attackEvent.getDefender())) return;
		attackEvent.increasePercent += 30;

		hitPlayers.get(attackEvent.getAttackerPlayer()).add(attackEvent.getDefender());
		new BukkitRunnable() {
			@Override
			public void run() {
				hitPlayers.get(attackEvent.getAttackerPlayer()).remove(attackEvent.getDefender());
			}
		}.runTaskLater(PitSim.INSTANCE, 120L);
	}

	@Override
	public List<String> getDescription() {
		return new ALoreBuilder("&7First hit on a player or", "&7bot deals &c+30% damage.").getLore();
	}
}
