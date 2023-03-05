package dev.kyro.pitsim.enchants.tainted.scythe;

import dev.kyro.pitsim.controllers.Cooldown;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.cosmetics.particles.FireworkSparkParticle;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.PitPlayerAttemptAbilityEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import dev.kyro.pitsim.misc.Sounds;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ElectricShock extends PitEnchant {
	public static ElectricShock INSTANCE;

	public ElectricShock() {
		super("Electric Shock", true, ApplyType.SCYTHES,
				"electricshock", "shock", "electric");
		isTainted = true;
		INSTANCE = this;
	}

	@EventHandler
	public void onUse(PitPlayerAttemptAbilityEvent event) {
		Player player = event.getPlayer();

		int enchantLvl = event.getEnchantLevel(this);
		if(enchantLvl == 0) return;

		Cooldown cooldown = getCooldown(player, 10);
		if(cooldown.isOnCooldown()) return;
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		if(!pitPlayer.useManaForSpell(getManaCost(enchantLvl))) {
			Sounds.NO.play(event.getPlayer());
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

		List<Entity> excluded = new ArrayList<>();
		excluded.add(player);
		excluded.add(target);
		chain(player, player, target, 0, getMaxBounces(enchantLvl), excluded);
		Sounds.ELECTRIC_SHOCK.play(player);
	}

	private static List<LivingEntity> getPossibleTargets(Location location, List<Entity> excluded) {
		List<LivingEntity> possibleTargets = new ArrayList<>();
		for(Entity entity : location.getWorld().getNearbyEntities(location, 7, 7, 7)) {
			if(!Misc.isValidMobPlayerTarget(entity) || excluded.contains(entity)) continue;
			LivingEntity livingEntity = (LivingEntity) entity;
			possibleTargets.add(livingEntity);
		}
		return possibleTargets;
	}

	private static void chain(Player player, LivingEntity startingEntity, LivingEntity endingEntity, int bounceNumber, int maxBounces, List<Entity> excluded) {
		Location startLocation = startingEntity.getLocation().add(0, startingEntity.getEyeHeight(), 0);
		Location endLocation = endingEntity.getLocation().add(0, endingEntity.getEyeHeight(), 0);
		double distance = startLocation.distance(endLocation);
		int steps = (int) Math.ceil(distance * 4);
		double stepSize = distance / steps;
		Vector stepVector = endLocation.toVector().subtract(startLocation.toVector()).normalize().multiply(stepSize);

		Location drawLocation = startLocation.clone();
		for(int i = 0; i <= steps; i++) {
			if(i >= 2 || bounceNumber != 0) {
				if(bounceNumber != 0 && Math.random() < 0.07) littleSpark(drawLocation.clone());
				drawEffect(drawLocation);
			}
			drawLocation.add(stepVector);
		}

		excluded.add(endingEntity);
		endingEntity.damage(5, player);

		if(bounceNumber >= maxBounces) return;
		bounceNumber++;
		for(LivingEntity possibleTarget : getPossibleTargets(endLocation, excluded))
			chain(player, endingEntity, possibleTarget, bounceNumber, maxBounces, excluded);
	}

	private static void littleSpark(Location location) {
		Vector vector = new Vector(Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5).normalize();
		int length = new Random().nextInt(5) + 3;
		for(int i = 0; i < length; i++) {
			location.add(vector.clone().multiply(0.25));
			drawEffect(location);
		}
	}

	private static void drawEffect(Location location) {
		for(Entity nearbyEntity : location.getWorld().getNearbyEntities(location, 25, 25, 25)) {
			if(!(nearbyEntity instanceof Player)) continue;
			EntityPlayer entityPlayer = ((CraftPlayer) nearbyEntity).getHandle();
			new FireworkSparkParticle().display(entityPlayer, location);
		}
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Right-Clicking while looking at a mob casts this spell for &b" + getManaCost(enchantLvl) +
						" mana&7, shooting an electric beam that chains between mobs up to &e" +
						getMaxBounces(enchantLvl) + " time" + (getMaxBounces(enchantLvl) == 1 ? "" : "s")
		).getLore();
	}

	public static int getManaCost(int enchantLvl) {
		return 1;
	}

	public static int getMaxBounces(int enchantLvl) {
		return enchantLvl;
	}
}
