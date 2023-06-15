package net.pitsim.spigot.cosmetics.misc.kyrocosmetic;

import net.pitsim.spigot.misc.Misc;
import net.pitsim.spigot.misc.Sounds;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Random;

public class LeechParticle extends AIParticle {
	public State state;
	public int timesToIdle;
	public int ticksUntilGoal;
	public Vector stepVector;

	public LeechParticle(Player owner) {
		super(owner, null, getIdleLocation(owner));
		setIdle();
	}

	@Override
	public void tick() {
		if(state == State.ATTACK && target.getLocation().distance(owner.getLocation()) > 25) setIdle();
		if(state == State.ATTACK) {
			updateAttackStepVector();
			double distance = particleLocation.distance(target.getLocation().add(0, 1, 0));
			if(distance < 0.5) {
				target.setHealth(Math.max(target.getHealth() - 2, 1));
				state = State.RETURN;
				Misc.applyPotionEffect(target, PotionEffectType.BLINDNESS, 20, 0, true, false);
				Sounds.KYRO_LIFESTEAL_LOSE.play(target);
			}
		}
		if(state == State.RETURN) {
			updateReturnStepVector();
			double distance = particleLocation.distance(owner.getLocation().add(0, 1, 0));
			if(distance < 0.5) {
				setIdle();
				owner.setHealth(Math.min(owner.getHealth() + 2, owner.getMaxHealth()));
				Sounds.KYRO_LIFESTEAL_GAIN.play(owner);
				Location newIdleLocation = getIdleLocation();
				updateIdleStepVector(newIdleLocation);
			}
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
		display(Effect.HEART);
	}

	public void setIdle() {
		state = State.IDLE;
		timesToIdle = new Random().nextInt(3) + 2;
		ticksUntilGoal = 0;
	}

	public void updateIdleStepVector(Location newIdleLocation) {
		int steps = new Random().nextInt(40) + 20;
		stepVector = newIdleLocation.toVector().subtract(particleLocation.toVector()).multiply(1.0 / steps);
		ticksUntilGoal = steps;
	}

	public void updateAttackStepVector() {
		stepVector = target.getLocation().add(0, 1, 0).toVector().subtract(particleLocation.toVector()).normalize().multiply(0.35);
	}

	public void updateReturnStepVector() {
		stepVector = owner.getLocation().add(0, 1, 0).toVector().subtract(particleLocation.toVector()).normalize().multiply(0.2);
	}

	public Location getIdleLocation() {
		return getIdleLocation(owner);
	}

	public static Location getIdleLocation(Player owner) {
		Location playerLocation = owner.getLocation();
		return playerLocation.add(Misc.randomOffset(8), Misc.randomOffsetPositive(2) + 2.5, Misc.randomOffset(8));
	}

	public enum State {
		IDLE,
		ATTACK,
		RETURN
	}
}
