package net.pitsim.spigot.darkzone.abilities;

import net.pitsim.spigot.PitSim;
import net.pitsim.spigot.darkzone.PitBossAbility;
import net.pitsim.spigot.controllers.DamageManager;
import net.pitsim.spigot.enums.PitEntityType;
import net.pitsim.spigot.misc.Misc;
import net.pitsim.spigot.misc.Sounds;
import net.pitsim.spigot.misc.effects.FallingBlock;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class SnakeAbility extends PitBossAbility {
	public int length;
	public double damage;
	public Material blockType;
	public byte blockData;
	public Sounds.SoundEffect effect;

	public SnakeAbility(double routineWeight, int length, double damage, Material blockType, byte blockData, Sounds.SoundEffect effect) {
		super(routineWeight);
		this.length = length;
		this.damage = damage;
		this.blockType = blockType;
		this.blockData = blockData;
		this.effect = effect;
	}

	@Override
	public void onRoutineExecute() {
		Vector direction = getPitBoss().getBoss().getLocation().getDirection().multiply(15);
		Location origin = getPitBoss().getBoss().getLocation();
		direction.divide(new Vector(length, length, length));

		int time = 0;

		for(int i = 0; i < length; i++) {
			new BukkitRunnable() {
				@Override
				public void run() {
					origin.add(direction);
					FallingBlock fallingBlock = new FallingBlock(blockType, blockData, origin);
					fallingBlock.setViewers(getViewers());
					fallingBlock.spawnBlock();
					fallingBlock.removeAfter(10);
					fallingBlock.setVelocity(new Vector(0, 0.2, 0));
					effect.play(origin, 20);

					for(Entity entity : origin.getWorld().getNearbyEntities(origin, 1.5, 1.5, 1.5)) {
						if(!Misc.isEntity(entity, PitEntityType.REAL_PLAYER)) continue;
						Player target = (Player) entity;

						target.setNoDamageTicks(0);
						DamageManager.createIndirectAttack(getPitBoss().getBoss(), target, damage);
						Misc.applyPotionEffect(target, PotionEffectType.SLOW, 20, 1, false, false);
					}
				}
			}.runTaskLater(PitSim.INSTANCE, time);
			time += 1;
		}
	}
}

