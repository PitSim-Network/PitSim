package dev.kyro.pitsim.adarkzone.mobs;

import dev.kyro.pitsim.adarkzone.DarkzoneManager;
import dev.kyro.pitsim.adarkzone.DropPool;
import dev.kyro.pitsim.adarkzone.PitMob;
import dev.kyro.pitsim.adarkzone.PitNameTag;
import dev.kyro.pitsim.aitems.mobdrops.Gunpowder;
import dev.kyro.pitsim.controllers.ItemFactory;
import dev.kyro.pitsim.controllers.PlayerManager;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.util.Vector;

public class PitCreeper extends PitMob {
	public static final double EXPLOSION_RADIUS = 6;

	public PitCreeper(Location spawnLocation) {
		super(spawnLocation);
	}

	@EventHandler
	public void onPrime(ExplosionPrimeEvent event) {
		Entity entity = event.getEntity();
		if(!isThisMob(entity)) return;
		if(!(entity instanceof LivingEntity)) return;
		LivingEntity livingEntity = (LivingEntity) entity;
		PitMob pitMob = DarkzoneManager.getPitMob(livingEntity);
		if(!(pitMob instanceof PitCreeper)) return;

		event.setCancelled(true);

		Location vectorStart = entity.getLocation().subtract(0, 0.5, 0);
		for(Entity nearbyEntity : entity.getNearbyEntities(EXPLOSION_RADIUS, EXPLOSION_RADIUS, EXPLOSION_RADIUS)) {
			if(!(nearbyEntity instanceof Player)) continue;
			Player player = (Player) nearbyEntity;
			if(!PlayerManager.isRealPlayer(player)) continue;

			double distance = player.getLocation().distance(entity.getLocation());
			if(distance > EXPLOSION_RADIUS) continue;
			double multiplier = Math.pow(EXPLOSION_RADIUS - distance, 1.5);
			Vector velocity = player.getLocation().subtract(vectorStart).toVector().normalize().multiply(0.3).multiply(multiplier);
			player.setVelocity(velocity);

			double damage = multiplier * 15;
			player.damage(damage, entity);
		}

		Location effectLocation = entity.getLocation().add(0, 1, 0);
		effectLocation.getWorld().playEffect(effectLocation, Effect.EXPLOSION_HUGE, 1);
		Sounds.CREEPER_EXPLODE.play(effectLocation);
	}

	@Override
	public Creature createMob(Location spawnLocation) {
		Creeper creeper = spawnLocation.getWorld().spawn(spawnLocation, Creeper.class);
		creeper.setCustomNameVisible(false);
		creeper.setRemoveWhenFarAway(false);
		creeper.setCanPickupItems(false);

		creeper.setPowered(true);

		return creeper;
	}

	@Override
	public String getRawDisplayName() {
		return "Creeper";
	}

	@Override
	public ChatColor getChatColor() {
		return ChatColor.GREEN;
	}

	@Override
	public int getMaxHealth() {
		return 160;
	}

	@Override
	public int getSpeedAmplifier() {
		return 3;
	}

	@Override
	public int getDroppedSouls() {
		return 8;
	}

	@Override
	public DropPool createDropPool() {
		return new DropPool()
				.addItem(ItemFactory.getItem(Gunpowder.class).getItem(), 1);
	}

	@Override
	public PitNameTag createNameTag() {
		return new PitNameTag(this, PitNameTag.NameTagType.NAME_AND_HEALTH)
				.addMob(PitNameTag.RidingType.BABY_RABBIT);
	}
}