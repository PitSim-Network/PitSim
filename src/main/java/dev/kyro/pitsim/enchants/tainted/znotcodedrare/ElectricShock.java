package dev.kyro.pitsim.enchants.tainted.znotcodedrare;

import dev.kyro.pitsim.controllers.Cooldown;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.PitPlayerAttemptAbilityEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import dev.kyro.pitsim.misc.Sounds;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

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

		Cooldown cooldown = getCooldown(event.getPlayer(), 10);
		if(cooldown.isOnCooldown()) return;
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(event.getPlayer());
		if(!pitPlayer.useMana(getManaCost(enchantLvl))) {
			Sounds.NO.play(event.getPlayer());
			return;
		}
		cooldown.restart();

//		pick initial target
		Location testLocation = player.getLocation();
		LivingEntity target = null;
		for(int i = 0; i < 20; i++) {
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
		for(int i = 0; i < steps; i++) {
			if(i >= 2 || bounceNumber != 0) drawEffect(drawLocation);
			drawLocation.add(stepVector);
		}

		excluded.add(endingEntity);
		endingEntity.damage(5, player);

		if(bounceNumber >= maxBounces) return;
		bounceNumber++;
		for(LivingEntity possibleTarget : getPossibleTargets(endLocation, excluded))
			chain(player, endingEntity, possibleTarget, bounceNumber, maxBounces, excluded);
	}

	private static void drawEffect(Location location) {
		for(Entity nearbyEntity : location.getWorld().getNearbyEntities(location, 25, 25, 25)) {
			if(!(nearbyEntity instanceof Player)) continue;
			EntityPlayer entityPlayer = ((CraftPlayer) nearbyEntity).getHandle();
			entityPlayer.playerConnection.sendPacket(new PacketPlayOutWorldParticles(
					EnumParticle.FIREWORKS_SPARK, true, (float) location.getX(), (float) location.getY(), (float) location.getZ(),
					0, 0, 0, 0, 0
			));
		}
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Chains between mobs up to &e" + getMaxBounces(enchantLvl) + "time" +
				(getMaxBounces(enchantLvl) == 1 ? "" : "s")
		).getLore();
	}

	public static int getManaCost(int enchantLvl) {
		return 1;
	}

	public static int getMaxBounces(int enchantLvl) {
		return enchantLvl;
	}
}
