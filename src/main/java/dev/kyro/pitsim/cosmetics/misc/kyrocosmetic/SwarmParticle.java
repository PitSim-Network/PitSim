package dev.kyro.pitsim.cosmetics.misc.kyrocosmetic;

import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

import java.util.Random;

public class SwarmParticle extends AIParticle {
	public State state = State.IDLE;
	public int ticksUntilGoal = 0;
	public Vector stepVector;
	public int cooldownTicks = 0;

	public static final double DAMAGE = 5;

	public SwarmParticle(Player owner) {
		super(owner, null, getIdleLocation(owner));
	}

	@Override
	public void tick() {
		if(cooldownTicks > 0) cooldownTicks--;
		if(state == State.ATTACK && target.getLocation().distance(owner.getLocation()) > 25) {
			state = State.IDLE;
			cooldownTicks = new Random().nextInt(40) + 80;
			ticksUntilGoal = 0;
		}
		if(state == State.ATTACK) {
			double distance = particleLocation.distance(target.getLocation().add(0, 1, 0));
			if(distance < 1.5) {
				EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(owner, target, EntityDamageEvent.DamageCause.CUSTOM, DAMAGE);
				Bukkit.getPluginManager().callEvent(event);
				if(!event.isCancelled()) target.damage(DAMAGE, owner);
			}
			if(ticksUntilGoal == 0) {
				state = State.IDLE;
				cooldownTicks = new Random().nextInt(40) + 80;
			}
		}
		if(state == State.IDLE && ticksUntilGoal == 0) {
			LivingEntity target = pickTarget();
			if(target == null || cooldownTicks != 0) {
				Location newIdleLocation = getIdleLocation();
				updateIdleStepVector(newIdleLocation);
			} else {
				state = State.ATTACK;
				updateAttackStepVector();
			}
		}
		particleLocation.add(stepVector);
		ticksUntilGoal--;
		display(Effect.VILLAGER_THUNDERCLOUD);
	}

	public void updateIdleStepVector(Location newIdleLocation) {
		int steps = new Random().nextInt(40) + 20;
		stepVector = newIdleLocation.toVector().subtract(particleLocation.toVector()).multiply(1.0 / steps);
		ticksUntilGoal = steps;
	}

	public void updateAttackStepVector() {
		stepVector = target.getLocation().add(0, 1, 0).toVector().subtract(particleLocation.toVector());
		ticksUntilGoal = (int) (stepVector.length() / 1) + 1;
		stepVector.multiply(1.0 / ticksUntilGoal);
		ticksUntilGoal += 10;
	}

	public Location getIdleLocation() {
		return getIdleLocation(owner);
	}

	public static Location getIdleLocation(Player owner) {
		Location playerLocation = owner.getLocation();
		return playerLocation.add(Misc.randomOffset(8), Misc.randomOffsetPositive(4) + 3, Misc.randomOffset(8));
	}

	public enum State {
		IDLE,
		ATTACK,
	}
}
