package net.pitsim.spigot.darkzone.mobs;

import net.pitsim.spigot.PitSim;
import net.pitsim.spigot.darkzone.*;
import net.pitsim.spigot.items.mobdrops.BlazeRod;
import net.pitsim.spigot.controllers.ItemFactory;
import net.pitsim.spigot.enums.MobStatus;
import net.pitsim.spigot.misc.CustomPitBlaze;
import net.pitsim.spigot.misc.EntityManager;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.Random;

public class PitBlaze extends PitMob {
	public long lastAttack = PitSim.currentTick;
	public BukkitTask attackRunnable;

	static {
		EntityManager.registerEntity("PitBlaze", 61, CustomPitBlaze.class);
	}

	public PitBlaze(Location spawnLocation, MobStatus mobStatus) {
		super(spawnLocation, mobStatus);
		if(spawnLocation == null) return;

		attackRunnable = new BukkitRunnable() {
			@Override
			public void run() {
				if(lastAttack + 40 > PitSim.currentTick) return;
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
		Location targetLocation = target.getLocation().add(0, 1, 0).add(target.getVelocity().multiply(2));
		Location source = mobLocation.add(mobLocation.getDirection().normalize().multiply(1.5));
		Vector direction = targetLocation.clone().subtract(mobLocation).toVector().normalize();

		Fireball fireball = source.getWorld().spawn(source, Fireball.class);
		fireball.setShooter(getMob());
		fireball.setDirection(direction);
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
	public SubLevelType getSubLevelType() {
		return SubLevelType.BLAZE;
	}

	@Override
	public Creature createMob(Location spawnLocation) {
		World nmsWorld = ((CraftWorld) spawnLocation.getWorld()).getHandle();

		CustomPitBlaze blaze = new CustomPitBlaze(nmsWorld);
		blaze.setLocation(spawnLocation.getX(), spawnLocation.getY() + 5, spawnLocation.getZ(), 0, 0);

		((LivingEntity) blaze.getBukkitEntity()).setRemoveWhenFarAway(false);

		nmsWorld.addEntity(blaze);

		return (Creature) blaze.getBukkitEntity();
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
		return DarkzoneBalancing.getAttributeAsInt(getSubLevelType(), DarkzoneBalancing.Attribute.MOB_HEALTH);
	}

	@Override
	public double getDamage() {
		return DarkzoneBalancing.getAttribute(getSubLevelType(), DarkzoneBalancing.Attribute.MOB_DAMAGE);
	}

	@Override
	public int getSpeedAmplifier() {
		return 1;
	}

	@Override
	public int getDroppedSouls() {
		return DarkzoneBalancing.getAttributeAsRandomInt(getSubLevelType(), DarkzoneBalancing.Attribute.MOB_SOULS);
	}

	@Override
	public DropPool createDropPool() {
		return new DropPool()
				.addRareItem(() -> ItemFactory.getItem(BlazeRod.class).getItem(), DarkzoneBalancing.MOB_ITEM_DROP_PERCENT);
	}

	@Override
	public PitNameTag createNameTag() {
		return new PitNameTag(this, PitNameTag.NameTagType.NAME_AND_HEALTH)
				.addMob(PitNameTag.RidingType.SMALL_MAGMA_CUBE);
	}
}