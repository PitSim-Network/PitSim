package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.Cooldown;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.Vector;

import java.util.List;

public class Explosive extends PitEnchant {

	public Explosive() {
		super("Explosive", true, ApplyType.BOWS,
				"explosive", "explo", "ex", "explode");
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canAttack(attackEvent)) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;

	}

	@EventHandler
	public void onShoot(ProjectileHitEvent event) {

		if(!(event.getEntity() instanceof Arrow) || !(event.getEntity().getShooter() instanceof Player)) return;

		Arrow arrow = (Arrow) event.getEntity();
		Player shooter = (Player) arrow.getShooter();

		int enchantLvl = EnchantManager.getEnchantLevel(shooter, this);
		if(enchantLvl == 0) return;

		Cooldown cooldown = getCooldown(shooter, getCooldown(enchantLvl));
		if(cooldown.isOnCooldown()) return; else cooldown.reset();

		for (Entity entity : arrow.getNearbyEntities(getRange(enchantLvl),
				getRange(enchantLvl), getRange(enchantLvl))) {
			if(entity instanceof Player) {
				Player player = (Player) entity;

				if(player != shooter) {
					Vector force = player.getLocation().toVector().subtract(arrow.getLocation().toVector())
							.setY(1).normalize().multiply(1.15);
//					force.setY(.85f);

					player.setVelocity(force);
				}
			}
		}

		arrow.getWorld().playSound(arrow.getLocation(), Sound.EXPLODE, 0.75f,
				getPitch(enchantLvl));
		arrow.getWorld().playEffect(arrow.getLocation(), getEffect(enchantLvl),
				getEffect(enchantLvl).getData(), 100);
	}

	@Override
	public List<String> getDescription(int enchantLvl) {


		if(enchantLvl == 1) {
			return new ALoreBuilder("&7Arrows fo POP! (" + getCooldown(enchantLvl) / 20 + "s cooldown)").getLore();
		} else {
			return new ALoreBuilder("&7Arrows fo BOOM! (" + getCooldown(enchantLvl) / 20 + "s cooldown)").getLore();
		}

	}

	public double getRange(int enchantLvl) {

		switch(enchantLvl) {
			case 1:
				return 1;
			case 2:
				return 2.5;
			case 3:
				return 6;

		}
		return 0;
	}

	public float getPitch(int enchantLvl) {

		switch(enchantLvl) {
			case 1:
				return 2;
			case 2:
				return 1;
			case 3:
				return 1.4F;

		}
		return 0;
	}

	public Effect getEffect(int enchantLvl) {

		switch(enchantLvl) {
			case 1:
				return Effect.EXPLOSION_LARGE;
			case 2:
				return Effect.EXPLOSION_HUGE;
			case 3:
				return Effect.EXPLOSION_HUGE;

		}
		return null;
	}

	public int getCooldown(int enchantLvl) {

		switch(enchantLvl) {
			case 1:
				return 100;
			case 2:
				return 60;
			case 3:
				return 100;

		}
		return 0;
	}

}
