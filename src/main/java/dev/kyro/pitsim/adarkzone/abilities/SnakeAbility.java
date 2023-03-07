package dev.kyro.pitsim.adarkzone.abilities;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.RoutinePitBossAbility;
import dev.kyro.pitsim.controllers.DamageManager;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import dev.kyro.pitsim.misc.effects.FallingBlock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class SnakeAbility extends RoutinePitBossAbility {
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
		Vector direction = pitBoss.boss.getLocation().getDirection().multiply(15);
		Location origin = pitBoss.boss.getLocation();
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
						if(!(entity instanceof Player)) continue;
						if(entity == pitBoss.boss) continue;

						DamageManager.createAttack(pitBoss.boss, (Player) entity, damage);
						Misc.applyPotionEffect((Player) entity, PotionEffectType.SLOW, 20, 1, false, false);
					}
				}
			}.runTaskLater(PitSim.INSTANCE, time);
			time += 1;
		}
	}
}

