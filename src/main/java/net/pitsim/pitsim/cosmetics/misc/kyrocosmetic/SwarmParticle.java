package net.pitsim.pitsim.cosmetics.misc.kyrocosmetic;

import net.pitsim.pitsim.controllers.DamageManager;
import net.pitsim.pitsim.misc.Misc;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Random;

public class SwarmParticle extends AIParticle {
	public State state;
	public int timesToIdle;
	public int ticksUntilGoal;
	public Vector stepVector;

	public static final double DAMAGE = 10;

	public SwarmParticle(Player owner) {
		super(owner, null, getIdleLocation(owner));
		setIdle();
	}

	@Override
	public void tick() {
		if(state == State.ATTACK && target.getLocation().distance(owner.getLocation()) > 25) setIdle();
		if(state == State.ATTACK) {
			double distance = particleLocation.distance(target.getLocation().add(0, 1, 0));
			if(distance < 1.5) DamageManager.createIndirectAttack(owner, target, DAMAGE);
			if(ticksUntilGoal == 0) setIdle();
		}
		if(state == State.IDLE && ticksUntilGoal == 0) {
			if(timesToIdle == 0) {
				pickTarget();
				if(target == null) {
					Location newIdleLocation = getIdleLocation();
					updateIdleStepVector(newIdleLocation);
				} else {
					state = State.ATTACK;
					updateAttackStepVector();
				}
			} else {
				timesToIdle--;
				Location newIdleLocation = getIdleLocation();
				updateIdleStepVector(newIdleLocation);
			}
		}
		particleLocation.add(stepVector);
		ticksUntilGoal--;
		display(Effect.VILLAGER_THUNDERCLOUD);
	}

	public void setIdle() {
		state = State.IDLE;
		timesToIdle = new Random().nextInt(3) + 3;
		ticksUntilGoal = 0;
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
