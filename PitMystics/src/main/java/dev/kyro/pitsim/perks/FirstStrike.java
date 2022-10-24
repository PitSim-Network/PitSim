package dev.kyro.pitsim.perks;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PitPerk;
import dev.kyro.pitsim.events.AttackEvent;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirstStrike extends PitPerk {
	public Map<LivingEntity, List<LivingEntity>> hitPlayers = new HashMap<>();

	public static FirstStrike INSTANCE;

	public FirstStrike() {
		super("First Strike", "firststrike", new ItemStack(Material.COOKED_CHICKEN), 15, true, "FIRST_STRIKE", INSTANCE, false);
		INSTANCE = this;
	}

	@EventHandler
	public void onHit(AttackEvent.Apply attackEvent) {

		if(!playerHasUpgrade(attackEvent.getAttacker())) return;

		if(!hitPlayers.containsKey(attackEvent.getAttacker())) hitPlayers.put(attackEvent.getAttacker(), new ArrayList<>());
		List<LivingEntity> hitList = hitPlayers.get(attackEvent.getAttacker());

		if(!hitList.contains(attackEvent.getDefender())) {
			attackEvent.increasePercent += 30 / 100D;
//			Misc.applyPotionEffect(attackEvent.getAttacker(), PotionEffectType.SPEED, 5 * 20, 0, false, false);
		}

		List<LivingEntity> newList = new ArrayList<>(hitList);
		newList.add(attackEvent.getDefender());
		hitPlayers.put(attackEvent.getAttacker(), newList);

		new BukkitRunnable() {
			@Override
			public void run() {
				hitPlayers.get(attackEvent.getAttacker()).remove(attackEvent.getDefender());
			}
		}.runTaskLater(PitSim.INSTANCE, 120L);
	}

	@Override
	public List<String> getDescription() {
		return new ALoreBuilder("&7First hit on a player deals", "&c+30% damage.").getLore();
	}
}
