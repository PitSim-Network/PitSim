package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.DamageEvent;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

public class Robinhood extends PitEnchant {

	public Robinhood() {
		super("Robinhood", true, ApplyType.BOWS,
				"robinhood", "robin");
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canAttack(attackEvent)) return;
	}

	@EventHandler
	public void onBowShoot(EntityShootBowEvent event) {

		if(!(event.getEntity() instanceof Player) || !(event.getProjectile() instanceof Arrow)) return;
		Player player = ((Player) event.getEntity()).getPlayer();
		Arrow arrow = (Arrow) event.getProjectile();

		int enchantLvl = EnchantManager.getEnchantLevel(player, this);
		if(enchantLvl == 0) return;

		new BukkitRunnable() {
			Map.Entry<Player, Double> targetInfo = null;
			@Override
			public void run() {

				for(Entity nearbyEntity : arrow.getWorld().getNearbyEntities(arrow.getLocation(), 8, 8, 8)) {

					if(!(nearbyEntity instanceof Player) || nearbyEntity.equals(player)) continue;
					Player target = (Player) nearbyEntity;

					double distance = arrow.getLocation().distance(nearbyEntity.getLocation());
					if(targetInfo == null || (nearbyEntity != targetInfo.getKey() && distance < targetInfo.getValue())) {
						targetInfo = new AbstractMap.SimpleEntry<>(target, distance);
						break;
					}
				}

				if(targetInfo != null) {

					Vector arrowVector = arrow.getLocation().toVector();
					Vector targetVector = targetInfo.getKey().getLocation().toVector();
					targetVector.setY(targetVector.getY() + 2);

					Vector direction = targetVector.subtract(arrowVector).normalize();
					arrow.setVelocity(direction);
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 3L);
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7All your shots are homing but", "&7deal &c60% &7damage").getLore();
	}
}
