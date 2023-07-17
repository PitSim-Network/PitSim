package net.pitsim.spigot.darkzone.mobs;

import net.pitsim.spigot.darkzone.*;
import net.pitsim.spigot.items.mobdrops.Gunpowder;
import net.pitsim.spigot.controllers.DamageManager;
import net.pitsim.spigot.controllers.ItemFactory;
import net.pitsim.spigot.controllers.PlayerManager;
import net.pitsim.spigot.enums.MobStatus;
import net.pitsim.spigot.misc.Sounds;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.util.Vector;

public class PitCreeper extends PitMob {
	public static final double EXPLOSION_RADIUS = 6;

	public PitCreeper(Location spawnLocation, MobStatus mobStatus) {
		super(spawnLocation, mobStatus);
	}

	@Override
	public SubLevelType getSubLevelType() {
		return SubLevelType.CREEPER;
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

			double damage = multiplier * getDamage() * 1.5;
			DamageManager.createDirectAttack((LivingEntity) entity, player, damage);
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
		return isMinion() ? "Minion Creeper" : "Creeper";
	}

	@Override
	public ChatColor getChatColor() {
		return ChatColor.GREEN;
	}

	@Override
	public int getMaxHealth() {
		int maxHealth = DarkzoneBalancing.getAttributeAsInt(getSubLevelType(), DarkzoneBalancing.Attribute.MOB_HEALTH);
		return isMinion() ? (int) (maxHealth * 1.25) : maxHealth;
	}

	@Override
	public double getDamage() {
		return DarkzoneBalancing.getAttribute(getSubLevelType(), DarkzoneBalancing.Attribute.MOB_DAMAGE) * 0.75;
	}

	@Override
	public int getSpeedAmplifier() {
		return isMinion() ? 5 : 3;
	}

	@Override
	public int getDroppedSouls() {
		return DarkzoneBalancing.getAttributeAsRandomInt(getSubLevelType(), DarkzoneBalancing.Attribute.MOB_SOULS);
	}

	@Override
	public DropPool createDropPool() {
		return new DropPool()
				.addRareItem(() -> ItemFactory.getItem(Gunpowder.class).getItem(), DarkzoneBalancing.MOB_ITEM_DROP_PERCENT);
	}

	@Override
	public PitNameTag createNameTag() {
		return new PitNameTag(this, PitNameTag.NameTagType.NAME_AND_HEALTH)
				.addMob(PitNameTag.RidingType.BABY_RABBIT);
	}
}