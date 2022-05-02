package dev.kyro.pitsim.controllers;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.brewing.BrewingManager;
import dev.kyro.pitsim.controllers.objects.PitMob;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.SubLevel;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.KillEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class MobManager implements Listener {
	public static List<PitMob> mobs = new ArrayList<>();
	public static Map<UUID, ArmorStand> nameTags = new HashMap<>();
	public static Map<ArmorStand, Location> locs = new HashMap<>();
	public static Map<ArmorStand, Location> oldLocs = new HashMap<>();


	static {
		new BukkitRunnable() {

			@Override
			public void run() {
				clearMobs();
				for(SubLevel level : SubLevel.values()) {


					int currentMobs = 0;
					for(PitMob mob : mobs) {
						if(mob.subLevel == level.level) currentMobs++;
					}

					if(currentMobs >= level.maxMobs) continue;


					Random xRand = new Random();
					int xLoc = xRand.nextInt(level.radius - (-1 * level.radius) + 1) + (-1 * level.radius);

					Random zRand = new Random();
					int zLoc = zRand.nextInt(level.radius - (-1 * level.radius) + 1) + (-1 * level.radius);

					Random rand = new Random();
					Class randClass = level.mobs.get(rand.nextInt(level.mobs.size()));
					try {

						Class[] cArg = new Class[1];
						cArg[0] = Location.class;

						Location loc = new Location(Bukkit.getWorld("darkzone"), xLoc + level.middle.getX() + 0.5, level.middle.getY(), zLoc + level.middle.getZ() + 0.5);
						while(loc.getBlock().getType() != Material.AIR) {
							loc.setY(loc.getY() + 1);
							if(loc.getY() >= level.middle.getY() + 10) continue;
						}

						randClass.getDeclaredConstructor(cArg).newInstance(loc);

//						randMob = (PitMob) randClass.newInstance();
					} catch(InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException ignored) {
					}
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 10, 10);

		new BukkitRunnable() {
			@Override
			public void run() {
				List<PitMob> toRemove = new ArrayList<>();
				for (PitMob mob : mobs) {

					if(!(mob.entity instanceof Monster)) continue;
					if(((Monster) mob.entity).getTarget() != null) continue;
					if(mob.entity.getNearbyEntities(1, 1, 1).size() <= 1) continue;
					nameTags.get(mob.entity.getUniqueId()).remove();
					mob.entity.remove();
					toRemove.add(mob);
				}

				for (PitMob pitMob : toRemove) {
					mobs.remove(pitMob);
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 20 * 20, 20 * 20);

		new BukkitRunnable() {
			@Override
			public void run() {
				List<PitMob> toRemove = new ArrayList<>();
				for(PitMob mob : mobs) {
					if(mob.entity.isDead()) {
						nameTags.get(mob.entity.getUniqueId()).remove();
						toRemove.add(mob);
					}
				}
				for(PitMob pitMob : toRemove) {
					mobs.remove(pitMob);
				}
			}

		}.runTaskTimer(PitSim.INSTANCE, 20 * 5, 20 * 5);

		new BukkitRunnable() {
			@Override
			public void run() {
				clearMobs();
			}
		}.runTaskLater(PitSim.INSTANCE, 10);
	}

	public static void makeTag(LivingEntity mob, String name) {
		Location op = mob.getLocation();
		ArmorStand stand = (ArmorStand) op.getWorld().spawnEntity(op, EntityType.ARMOR_STAND);
		stand.setGravity(false);
		stand.setVisible(true);
		stand.setCustomNameVisible(true);
		stand.setRemoveWhenFarAway(false);
		stand.setVisible(false);
		stand.setSmall(true);
		mob.setPassenger(stand);
		stand.setCustomName(ChatColor.translateAlternateColorCodes('&', name));

		nameTags.put(mob.getUniqueId(), stand);

//		nameTags.put(mob, stand);
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onKill(KillEvent event) {
		if(event.deadIsPlayer) return;
		clearMobs();
		List<PitMob> toRemove = new ArrayList<>();
		for(PitMob mob : mobs) {
			if(mob.entity.getUniqueId().equals(event.dead.getUniqueId())) {
				for (Entity entity : Bukkit.getWorld("darkzone").getEntities()) {
					if(entity.getUniqueId().equals(nameTags.get(mob.entity.getUniqueId()).getUniqueId())) {
						entity.remove();
					}
				}
				toRemove.add(mob);

				Map<ItemStack, Integer> drops = mob.getDrops();
				for (Map.Entry<ItemStack, Integer> entry : drops.entrySet()) {
					Random r = new Random();
					int low = 1;
					int high = 100;
					int result = r.nextInt(high-low) + low;

					if(result > entry.getValue()) continue;
					event.dead.getWorld().dropItemNaturally(event.dead.getLocation(), entry.getKey());
				}
			}
		}
		new BukkitRunnable() {
			@Override
			public void run() {
				for(PitMob pitMob : toRemove) {
					mobs.remove(pitMob);
				}
			}
		}.runTaskLater(PitSim.INSTANCE, 1);
	}

	@EventHandler
	public void onSpawn(EntitySpawnEvent event) {
		if(event.getLocation().getWorld() == Bukkit.getWorld("darkzone") && event.getEntity() instanceof Enderman && !event.getEntity().isCustomNameVisible()) event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onHit(AttackEvent.Pre event) {
		for (Villager value : BossManager.clickables.values()) {
			if(event.defender.getUniqueId().equals(value.getUniqueId())) event.setCancelled(true);
		}

		if(!(event.defender instanceof ArmorStand)) return;

		for(ArmorStand value : nameTags.values()) {
			if(event.defender.getUniqueId().equals(value.getUniqueId())) event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onHit(EntityDamageByEntityEvent event) {
		if(event.getDamager() instanceof Arrow) return;
		if(event.getDamager() instanceof Fireball) return;
		if(NonManager.getNon((LivingEntity) event.getDamager()) != null) return;

		for (Villager value : BossManager.clickables.values()) {
			if(event.getEntity().getUniqueId().equals(value.getUniqueId())) event.setCancelled(true);
		}

		if(!(event.getEntity() instanceof ArmorStand)) return;


		for(ArmorStand value : nameTags.values()) {
			if(event.getEntity().getUniqueId().equals(value.getUniqueId())) event.setCancelled(true);
		}
	}

	@EventHandler
	public void onEquip(PlayerArmorStandManipulateEvent event) {
		if(event.getRightClicked() == null) return;
		event.setCancelled(true);

		for(ArmorStand value : nameTags.values()) {
			if(event.getRightClicked().getUniqueId().equals(value.getUniqueId())) event.setCancelled(true);
		}
	}

	@EventHandler
	public void onExplode(ExplosionPrimeEvent event) {
		Entity entity = event.getEntity();
		if (!(entity instanceof Creeper)) return;

		PitMob mob = PitMob.getPitMob((LivingEntity) entity);
		if(mob == null) return;
		mobs.remove(mob);
		nameTags.get(mob.entity.getUniqueId()).remove();
		nameTags.remove(mob.entity.getUniqueId());
		event.setRadius(0);

		for (Entity player : entity.getNearbyEntities(5, 5, 5)) {
			if(!(player instanceof Player)) continue;

			PitPlayer.getPitPlayer((Player) player).damage(5.0, (LivingEntity) entity);
		}
	}

	public static void clearMobs() {
		main:
		for (Entity entity : Bukkit.getWorld("darkzone").getEntities()) {

			if(entity instanceof Player) continue;
			for (PitMob mob : mobs) {
				if(mob.entity.getUniqueId().equals(entity.getUniqueId())) continue main;
				if(nameTags.get(mob.entity.getUniqueId()).getUniqueId().equals(entity.getUniqueId())) continue main;
			}

			if(entity.getUniqueId().equals(TaintedWell.wellStand.getUniqueId())) continue;
//			if(entity.getUniqueId().equals(TaintedWell.removeStand.getUniqueId())) continue;
			for (ArmorStand value : TaintedWell.enchantStands.values()) {
				if(value.getUniqueId().equals(entity.getUniqueId())) continue main;
			}
			for (ArmorStand value : TaintedWell.removeStands.values()) {
				if(value.getUniqueId().equals(entity.getUniqueId())) continue main;
			}
			for (ArmorStand value : BrewingManager.brewingStands) {
				if(value.getUniqueId().equals(entity.getUniqueId())) continue main;
			}
			if(entity.getUniqueId().equals(TaintedWell.textLine1.getUniqueId())) continue;
			if(entity.getUniqueId().equals(TaintedWell.textLine2.getUniqueId())) continue;
			if(entity.getUniqueId().equals(TaintedWell.textLine3.getUniqueId())) continue;
			if(entity.getUniqueId().equals(TaintedWell.textLine4.getUniqueId())) continue;
			if(entity instanceof Item) continue;
			if(entity instanceof Arrow) continue;
			if(entity instanceof Wither) continue;
			if(entity instanceof Villager) continue;
			if(entity instanceof Fireball) continue;

			entity.remove();
		}
	}




}
