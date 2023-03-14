package dev.kyro.pitsim.adarkzone.mobs;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.DarkzoneManager;
import dev.kyro.pitsim.adarkzone.DropPool;
import dev.kyro.pitsim.adarkzone.PitMob;
import dev.kyro.pitsim.adarkzone.PitNameTag;
import dev.kyro.pitsim.aitems.mobdrops.BlazeRod;
import dev.kyro.pitsim.controllers.DamageManager;
import dev.kyro.pitsim.controllers.ItemFactory;
import dev.kyro.pitsim.enums.MobStatus;
import dev.kyro.pitsim.enums.PitEntityType;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.Random;

public class PitBlaze extends PitMob {

	public static final double FIREBALL_DAMAGE = 4;

	public long lastAttack = PitSim.currentTick;
	public BukkitTask attackRunnable;

	public PitBlaze(Location spawnLocation, MobStatus mobStatus) {
		super(spawnLocation, mobStatus);
		if(spawnLocation == null) return;

		attackRunnable = new BukkitRunnable() {
			@Override
			public void run() {
				if(lastAttack + 60 > PitSim.currentTick) return;
				if(Math.random() > 0.25) return;

				Player target = getTarget();
				if(target == null) return;

				lastAttack = PitSim.currentTick;
				throwFireball(target);
			}
		}.runTaskTimer(PitSim.INSTANCE, new Random().nextInt(5), 5L);
	}

	public void throwFireball(Player target) {
		Location mobLocation = getMob().getLocation().add(0, 1.5, 0);
		Location targetLocation = target.getLocation().add(0, 1, 0);
		Location source = mobLocation.add(mobLocation.getDirection().normalize());

		Vector direction = targetLocation.clone().subtract(mobLocation).toVector().normalize();

		Location stepLocation = source.clone();
		Vector stepVector = direction.clone().multiply(0.5);
		for(int i = 0; i < 20; i++) {
			stepLocation.getWorld().playEffect(stepLocation, Effect.HAPPY_VILLAGER, 1);
			stepLocation.add(stepVector);
		}

		Fireball fireball = source.getWorld().spawn(source, Fireball.class);
		fireball.setShooter(getMob());
		fireball.setDirection(direction);
	}

	@EventHandler
	public void onFireballExplode(EntityExplodeEvent event) {
		if(event.getEntity() instanceof Fireball) event.setCancelled(true);

		event.getLocation().getWorld().playEffect(event.getLocation(), Effect.EXPLOSION_LARGE, 1);
		for(Entity entity : event.getLocation().getWorld().getNearbyEntities(event.getLocation(), 3, 3, 3)) {
			if(!Misc.isEntity(entity, PitEntityType.REAL_PLAYER)) continue;
			DamageManager.createIndirectAttack(getMob(), (LivingEntity) entity, FIREBALL_DAMAGE);
		}
	}

	@EventHandler
	public void onFireballLaunch(ProjectileLaunchEvent event) {
		Projectile projectile = event.getEntity();
		ProjectileSource shooter = projectile.getShooter();
		if(!(shooter instanceof LivingEntity) || !(projectile instanceof Fireball)) return;
		LivingEntity livingEntity = (LivingEntity) shooter;
		if(!isThisMob(livingEntity)) return;

		PitMob pitMob = DarkzoneManager.getPitMob(livingEntity);
		if(pitMob instanceof PitBlaze) event.setCancelled(true);
	}

	@Override
	public void onDeath() {
		attackRunnable.cancel();
	}

	@Override
	public Creature createMob(Location spawnLocation) {
		Blaze blaze = spawnLocation.getWorld().spawn(spawnLocation, Blaze.class);
		blaze.setCustomNameVisible(false);
		blaze.setRemoveWhenFarAway(false);
		blaze.setCanPickupItems(false);

		return blaze;
	}

	@Override
	public String getRawDisplayName() {
		return isMinion() ? "Minion Blaze" : "Blaze";
	}

	@Override
	public ChatColor getChatColor() {
		return ChatColor.YELLOW;
	}

	@Override
	public int getMaxHealth() {
		return 100;
	}

	@Override
	public double getMeleeDamage() {
		return 10;
	}

	@Override
	public int getSpeedAmplifier() {
		return 1;
	}

	@Override
	public int getDroppedSouls() {
		return 5;
	}

	@Override
	public DropPool createDropPool() {
		return new DropPool()
				.addItem(ItemFactory.getItem(BlazeRod.class).getItem(), 1);
	}

	@Override
	public PitNameTag createNameTag() {
		return new PitNameTag(this, PitNameTag.NameTagType.NAME_AND_HEALTH)
				.addMob(PitNameTag.RidingType.SMALL_MAGMA_CUBE);
	}
}