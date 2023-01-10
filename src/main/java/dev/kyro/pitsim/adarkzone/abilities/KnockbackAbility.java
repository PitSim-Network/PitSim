package dev.kyro.pitsim.adarkzone.abilities;

import dev.kyro.pitsim.adarkzone.PitBossAbility;
import dev.kyro.pitsim.events.AttackEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.ArrayList;
import java.util.List;

public class KnockbackAbility extends PitBossAbility {
	public int intensity;

	public KnockbackAbility(int intensity) {
		super();
		this.intensity = intensity;
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!isAssignedBoss(attackEvent.getAttacker())) return;

		List<Player> playersInRadius = new ArrayList<>();
		for(Entity entity : attackEvent.getAttacker().getNearbyEntities(3, 3, 3))  {
			if(!(entity instanceof Player)) continue;
			Player player = (Player) entity;
			playersInRadius.add(player);
		}

		for(Player player : playersInRadius) {
			player.setVelocity(player.getLocation().toVector().
					subtract(attackEvent.getAttacker().getLocation().toVector()).
					normalize().multiply(intensity)
			);
		}
	}
}

