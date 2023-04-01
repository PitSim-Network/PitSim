package dev.kyro.pitsim.adarkzone.abilities;

import dev.kyro.pitsim.adarkzone.PitBossAbility;
import dev.kyro.pitsim.cosmetics.ParticleOffset;
import dev.kyro.pitsim.cosmetics.particles.ExplosionLargeParticle;
import dev.kyro.pitsim.cosmetics.particles.FlameParticle;
import dev.kyro.pitsim.cosmetics.particles.SmokeLargeParticle;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.entity.Player;

public class ChargeAbility extends PitBossAbility {
	public ChargeAbility(double routineWeight) {
		super(routineWeight);
	}

	@Override
	public void onRoutineExecute() {
		Player target = getPitBoss().bossTargetingSystem.target;
		if(target == null) return;

		Sounds.CHARGE.play(getPitBoss().boss.getLocation(), 30);

		SmokeLargeParticle smoke = new SmokeLargeParticle();
		FlameParticle flame = new FlameParticle();
		ExplosionLargeParticle explosion = new ExplosionLargeParticle();

		for(Player viewer : getViewers()) {
			for(int i = 0; i < 50; i++) {
				smoke.display(viewer, getPitBoss().boss.getLocation(), new ParticleOffset(1, 2, 1, 5, 4, 5));
				flame.display(viewer, getPitBoss().boss.getLocation(), new ParticleOffset(1, 2, 1, 5, 4, 5));
			}
			explosion.display(viewer, getPitBoss().boss.getLocation());
		}

		getPitBoss().boss.setVelocity(target.getLocation().toVector()
				.subtract(getPitBoss().boss.getLocation().toVector()).normalize().setY(0.2).normalize().multiply(1.8));
	}

	@Override
	public boolean shouldExecuteRoutine() {
		Player target = getPitBoss().bossTargetingSystem.target;
		if(target == null) return false;

		return target.getLocation().distance(getPitBoss().boss.getLocation()) >= 5;
	}
}
