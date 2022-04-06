package dev.kyro.pitsim.controllers;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PitMob;
import dev.kyro.pitsim.enums.SubLevel;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.KillEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.EquipmentSetEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class MobManager implements Listener {
	public static List<PitMob> mobs = new ArrayList<>();
	public static Map<LivingEntity, ArmorStand> nameTags = new HashMap<>();
	public static Map<ArmorStand, Location> locs = new HashMap<>();
	public static Map<ArmorStand, Location> oldLocs = new HashMap<>();


	static {
		new BukkitRunnable() {

			@Override
			public void run() {
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
					PitMob randMob = null;
					try {

						Class[] cArg = new Class[1];
						cArg[0] = Location.class;

						Location loc = new Location(Bukkit.getWorld("darkzone"), xLoc + level.middle.getX() + 0.5, level.middle.getY(), zLoc + level.middle.getZ() + 0.5);
						while(loc.getBlock().getType() != Material.AIR) {
							loc.setY(loc.getY() + 1);
							if(loc.getY() >= level.middle.getY() + 10) continue;
						}

						randMob = (PitMob) randClass.getDeclaredConstructor(cArg).newInstance(loc);
						System.out.println(randMob);

						randMob = (PitMob) randClass.newInstance();
					} catch(InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException ignored) {
					}
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 10, 10);

		new BukkitRunnable() {
			@Override
			public void run() {
				List<PitMob> toRemove = new ArrayList<>();
				for(PitMob mob : mobs) {
					if(mob.entity.isDead()) {
						nameTags.get(mob.entity).remove();
						toRemove.add(mob);
					}
				}
				for(PitMob pitMob : toRemove) {
					mobs.remove(pitMob);
				}
			}

		}.runTaskTimer(PitSim.INSTANCE, 20 * 5, 20 * 5);
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

		nameTags.put(mob, stand);

//		nameTags.put(mob, stand);
	}

	@EventHandler
	public void onKill(KillEvent event) {
		if(event.deadIsPlayer) return;
		List<PitMob> toRemove = new ArrayList<>();
		for(PitMob mob : mobs) {
			if(mob.entity == event.dead) {
				nameTags.get(event.dead).remove();
				toRemove.add(mob);
			}
		}
		for(PitMob pitMob : toRemove) {
			mobs.remove(pitMob);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onHit(AttackEvent.Pre event) {
		if(!(event.defender instanceof ArmorStand)) return;

		for(ArmorStand value : nameTags.values()) {
			if(event.defender == value) event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onHit(EntityDamageByEntityEvent event) {
		if(!(event.getEntity() instanceof ArmorStand)) return;

		for(ArmorStand value : nameTags.values()) {
			if(event.getEntity() == value) event.setCancelled(true);
		}
	}

	@EventHandler
	public void onEquip(PlayerArmorStandManipulateEvent event) {
		if(event.getRightClicked() == null) return;

		for(ArmorStand value : nameTags.values()) {
			if(event.getRightClicked() == value) event.setCancelled(true);
		}
	}




}
