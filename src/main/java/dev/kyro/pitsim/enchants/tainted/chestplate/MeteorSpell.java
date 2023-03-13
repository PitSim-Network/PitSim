package dev.kyro.pitsim.enchants.tainted.chestplate;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.Cooldown;
import dev.kyro.pitsim.controllers.DamageManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.PitPlayerAttemptAbilityEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

public class MeteorSpell extends PitEnchant {

	public MeteorSpell() {
		super("Meteor", true, ApplyType.SCYTHES,
				"meteor");
		isTainted = true;
	}

	@EventHandler
	public void onUse(PitPlayerAttemptAbilityEvent event) {
		Player player = event.getPlayer();

		int enchantLvl = event.getEnchantLevel(this);
		if(enchantLvl == 0) return;

		Cooldown cooldown = getCooldown(player, 10);
		if(cooldown.isOnCooldown()) {
			Sounds.NO.play(player);
			return;
		}
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		if(!pitPlayer.useManaForSpell(getManaCost(enchantLvl))) {
			Sounds.NO.play(player);
			return;
		}
		cooldown.restart();

//		pick initial target
		Location testLocation = player.getLocation();
		LivingEntity target = null;
		for(int i = 0; i < 40; i++) {
			LivingEntity closestEntity = Misc.getMobPlayerClosest(testLocation, 1, player);
			if(closestEntity == null) {
				testLocation.add(player.getLocation().getDirection().multiply(0.5));
				continue;
			}
			target = closestEntity;
			break;
		}

		if(target == null) {
			Sounds.NO.play(player);
			return;
		}

		double xOffset = Misc.randomOffset(10);
		double zOffset = Misc.randomOffset(10);
		int steps = 20;
		Location targetLocation = target.getLocation();
		Location effectLocation = targetLocation.clone().add(xOffset, 15, zOffset);
		Vector step = effectLocation.toVector().subtract(targetLocation.toVector()).multiply(-1.0 / steps);

		new BukkitRunnable() {
			int count = 0;
			@Override
			public void run() {
				if(count == steps) {
					cancel();
					Sounds.METEOR_2.play(effectLocation);

					for(Entity entity : effectLocation.getWorld().getNearbyEntities(effectLocation, 3, 3, 3)) {
						if(!Misc.isValidMobPlayerTarget(entity, player)) continue;
						LivingEntity livingEntity = (LivingEntity) entity;

						double distance = livingEntity.getLocation().distance(effectLocation);
						if(distance > 3) continue;

						DamageManager.createAttack(player, livingEntity, 50);
					}
				} else {
					effectLocation.getWorld().playEffect(effectLocation, Effect.EXPLOSION_LARGE, 1);
					effectLocation.getWorld().playEffect(effectLocation, Effect.LARGE_SMOKE, 1);
					effectLocation.getWorld().playEffect(effectLocation, Effect.PARTICLE_SMOKE, 1);
					effectLocation.add(step);
					if(count % 2 == 0) Sounds.METEOR.play(effectLocation);
				}

				count++;
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 1L);
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Summon a meteor, causing large damage to a single target &d&o-" + getManaCost(enchantLvl) + " Mana"
		).getLore();
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is a &5Darkzone &7enchant that " +
				"summons a meteor that deals a very high amount of damage";
	}

	public static int getManaCost(int enchantLvl) {
		if(true) return 1;
		return 120 - (30 * enchantLvl);
	}
}
