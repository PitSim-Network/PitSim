package dev.kyro.pitsim.adarkzone.abilities;

import dev.kyro.pitsim.adarkzone.RoutinePitBossAbility;
import dev.kyro.pitsim.cosmetics.ParticleOffset;
import dev.kyro.pitsim.cosmetics.particles.*;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.entity.Player;

public class ChargeAbility extends RoutinePitBossAbility {
	public ChargeAbility(double routineWeight) {
		super(routineWeight);
	}

	@Override
	public void onRoutineExecute() {
		Player target = pitBoss.bossTargetingSystem.target;
		if(target == null) return;

		Sounds.CHARGE.play(pitBoss.boss.getLocation(), 30);

		SmokeLargeParticle smoke = new SmokeLargeParticle();
		FlameParticle flame = new FlameParticle();
		ExplosionLargeParticle explosion = new ExplosionLargeParticle();

		for(Player viewer : getViewers()) {
			for(int i = 0; i < 50; i++) {
				smoke.display(viewer, pitBoss.boss.getLocation(), new ParticleOffset(1, 2, 1, 5, 4, 5));
				flame.display(viewer, pitBoss.boss.getLocation(), new ParticleOffset(1, 2, 1, 5, 4, 5));
			}
			explosion.display(viewer, pitBoss.boss.getLocation());
		}

		pitBoss.boss.setVelocity(target.getLocation().toVector().subtract(pitBoss.boss.getLocation().toVector()).normalize().multiply(2));
	}

	@Override
	public boolean shouldExecuteRoutine() {
		Player target = pitBoss.bossTargetingSystem.target;
		if(target == null) return false;

		return !(target.getLocation().distance(pitBoss.boss.getLocation()) < 5);
	}
}
