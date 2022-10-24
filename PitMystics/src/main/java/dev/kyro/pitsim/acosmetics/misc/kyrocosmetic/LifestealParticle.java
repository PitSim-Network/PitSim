package dev.kyro.pitsim.acosmetics.misc.kyrocosmetic;

import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class LifestealParticle extends AIParticle {
	public State state = State.IDLE;
	public int ticksUntilGoal = 0;
	public Vector stepVector;

	public LifestealParticle(Player owner) {
		super(owner, null, getIdleLocation(owner));
	}

	@Override
	public void tick() {
		if(state == State.ATTACK && target.getLocation().distance(owner.getLocation()) > 25) {
			pickTarget();
		}
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
				owner.setHealth(Math.min(owner.getHealth() + 2, owner.getMaxHealth()));
				state = State.IDLE;
				Sounds.KYRO_LIFESTEAL_GAIN.play(owner);
				Location newIdleLocation = getIdleLocation();
				updateIdleStepVector(newIdleLocation);
			}
		}
		if(state == State.IDLE && ticksUntilGoal == 0) {
			Player target = pickTarget();
			if(target == null) {
				Location newIdleLocation = getIdleLocation();
				updateIdleStepVector(newIdleLocation);
			} else {
				state = State.ATTACK;
				updateAttackStepVector();
			}
		}
		particleLocation.add(stepVector);
		ticksUntilGoal--;
		display(Effect.HEART);
	}

	public void updateIdleStepVector(Location newIdleLocation) {
		stepVector = newIdleLocation.toVector().subtract(particleLocation.toVector()).multiply(1.0 / 40);
		ticksUntilGoal = 40;
	}

	public void updateAttackStepVector() {
		stepVector = target.getLocation().add(0, 1, 0).toVector().subtract(particleLocation.toVector()).normalize().multiply(0.3);
	}

	public void updateReturnStepVector() {
		stepVector = owner.getLocation().add(0, 1, 0).toVector().subtract(particleLocation.toVector()).normalize().multiply(0.15);
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
