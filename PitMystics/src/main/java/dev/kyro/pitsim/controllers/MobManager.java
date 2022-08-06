package dev.kyro.pitsim.controllers;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.brewing.BrewingManager;
import dev.kyro.pitsim.controllers.objects.GoldenHelmet;
import dev.kyro.pitsim.controllers.objects.PitMob;
import dev.kyro.pitsim.enchants.tainted.CleaveSpell;
import dev.kyro.pitsim.enums.SubLevel;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.mobs.PitMagmaCube;
import dev.kyro.pitsim.mobs.PitSpiderBrute;
import dev.kyro.pitsim.mobs.PitStrongPigman;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
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

	public static final int MAX_TARGETS = 4;

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!attackEvent.attackerIsPlayer || attackEvent.defenderIsPlayer) return;
		if(!MapManager.inDarkzone(attackEvent.attackerPlayer.getLocation())) return;
		if(!(attackEvent.defender instanceof Creature)) return;
		PitMob pitMob = PitMob.getPitMob(attackEvent.defender);
		if(pitMob == null) return;
		if(attackEvent.attackerPlayer.getGameMode() == GameMode.SURVIVAL) {
			((Creature) attackEvent.defender).setTarget(attackEvent.attackerPlayer);
			pitMob.lastHit = System.currentTimeMillis();
			pitMob.target = attackEvent.attackerPlayer;
		}
	}

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Entity entity :new ArrayList<>(MapManager.getDarkzone().getEntities())) {

					if(!(entity instanceof LivingEntity)) continue;
					if(entity instanceof ArmorStand) continue;
					if(entity instanceof Player) continue;

					for(PitMob mob : new ArrayList<>(mobs)) {
						if(mob.entity.getUniqueId().equals(entity.getUniqueId())) {
							mob.entity = (LivingEntity) entity;
							break;
						}
					}
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0, 10);

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
							if(loc.getY() >= level.middle.getY() + 3) continue;
						}

						randClass.getDeclaredConstructor(cArg).newInstance(loc);

//						randMob = (PitMob) randClass.newInstance();
					} catch(InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException ignored) {
					}
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 40, 40);

		new BukkitRunnable() {
			@Override
			public void run() {
				List<PitMob> toRemove = new ArrayList<>();
				for (PitMob mob : mobs) {

					assert SubLevel.getLevel(mob.subLevel) != null;
					if(mob.entity.getLocation().distance(SubLevel.getLevel(mob.subLevel).middle) <= SubLevel.getLevel(mob.subLevel).radius + 10) {
						if(!(mob.entity instanceof Monster)) continue;
						if(((Monster) mob.entity).getTarget() != null) continue;
						if(mob.entity.getNearbyEntities(1, 1, 1).size() <= 1) continue;
					}
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

		new BukkitRunnable() {
			@Override
			public void run() {
				for(PitMob mob : MobManager.mobs) {
					if(!(mob.entity instanceof Creature)) continue;
					if(mob.target != null) ((Creature) mob.entity).setTarget(mob.target);
				}
				for(Player player : Bukkit.getOnlinePlayers()) {
					if(!MapManager.inDarkzone(player.getLocation())) continue;

					SubLevel subLevel = null;
					double distanceToClosest = 0;
                    for(SubLevel testSubLevel : SubLevel.values()) {
						double distance = player.getLocation().distance(testSubLevel.middle);
                        if(distance > testSubLevel.radius + 10) continue;
						if(subLevel != null && distance >= distanceToClosest) continue;
						subLevel = testSubLevel;
						distanceToClosest = distance;
                    }
					if(subLevel == null) continue;

					HashMap<PitMob, Double> noTarget = new HashMap<>();
					int targets = 0;

					List<PitMob> mobsCopy = new ArrayList<>(mobs);
					Collections.shuffle(mobsCopy);
					for(PitMob mob : mobsCopy) {
						if(mob instanceof PitStrongPigman || mob instanceof PitSpiderBrute || !(mob.entity instanceof Creature)) continue;
						if(mob.subLevel != subLevel.level) {
							if(mob.target == player) {
								mob.target = null;
								((Creature) mob.entity).setTarget(null);
							}
							continue;
						}
						if(mob.target == null || !mob.target.isOnline()) {
							noTarget.put(mob, mob.entity.getLocation().distance(player.getLocation()));
							continue;
						} else if(mob.target == player) {
							targets++;
							if(targets > MAX_TARGETS + 2) {
								mob.target = null;
								((Creature) mob.entity).setTarget(null);
								targets--;
								continue;
							}
							if(mob.lastHit + 10_000 < System.currentTimeMillis()) {
								mob.remove();
								targets--;
							}
						}
					}

					if(targets >= MAX_TARGETS) continue;
					noTarget = sortByValue(noTarget);
					for(Map.Entry<PitMob, Double> entry : noTarget.entrySet()) {
						PitMob pitMob = entry.getKey();
						if(player.getGameMode() == GameMode.SURVIVAL) {
							((Creature) pitMob.entity).setTarget(player);
							pitMob.lastHit = System.currentTimeMillis();
							pitMob.target = player;
							targets++;
						}
						if(targets >= MAX_TARGETS) break;
					}
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 15, 20);
	}

	// function to sort hashmap by values
	public static HashMap<PitMob, Double> sortByValue(HashMap<PitMob, Double> hm) {
		List<Map.Entry<PitMob, Double> > list =
				new LinkedList<>(hm.entrySet());
		list.sort(Map.Entry.comparingByValue());
		HashMap<PitMob, Double> temp = new LinkedHashMap<>();
		for (Map.Entry<PitMob, Double> aa : list) {
			temp.put(aa.getKey(), aa.getValue());
		}
		return temp;
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

				ItemStack helmet = GoldenHelmet.getHelmet(event.killerPlayer);

				int level = 0;
				double chance = 0;
				if(helmet != null) level = HelmetSystem.getLevel(GoldenHelmet.getUsedHelmetGold(event.deadPlayer));
				if(helmet != null) chance = 7.5 * HelmetSystem.getTotalStacks(HelmetSystem.Passive.DROP_CHANCE, level - 1);

				for (Map.Entry<ItemStack, Integer> entry : drops.entrySet()) {
					Random r = new Random();
					int low = 1;
					int high = 100;
					int result = r.nextInt(high-low) + low;

					result += result * (chance * 0.01);

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

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onHit(AttackEvent.Pre event) {

		if(event.attacker instanceof MagmaCube && (!(event.defender instanceof Player))) event.setCancelled(true);

		for (NPC value : BossManager.clickables.values()) {
			if(event.defender.getUniqueId().equals(value.getUniqueId())) event.setCancelled(true);
		}

		if(!(event.defender instanceof ArmorStand)) return;

		for(ArmorStand value : nameTags.values()) {
			if(event.defender.getUniqueId().equals(value.getUniqueId())) event.setCancelled(true);
		}
	}

	@EventHandler
	public void onMobAttack(EntityDamageByEntityEvent event) {
		if(event.getDamager() instanceof Player) return;
		if(!(event.getDamager() instanceof LivingEntity)) return;
		PitMob mob = PitMob.getPitMob((LivingEntity) event.getDamager());
		if(mob == null) return;

		mob.lastHit = System.currentTimeMillis();

		if(mob instanceof PitMagmaCube) return;

		if(event.getDamage() > 0) event.setDamage(mob.damage);
	}

	@EventHandler
	public void onBlockPickup(EntityChangeBlockEvent event) {
		if(event.getEntity() instanceof Enderman) event.setCancelled(true);
	}

	@EventHandler
	public void onTeleport(EntityTeleportEvent event) {
		if(event.getEntity() instanceof Enderman) event.setCancelled(true);
	}

	@EventHandler
	public void onTarget(EntityTargetLivingEntityEvent event) {
		if(event.getReason() != EntityTargetEvent.TargetReason.CUSTOM && (!(event.getEntity() instanceof Slime))) event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onHit(EntityDamageByEntityEvent event) {
		if(event.getDamager() instanceof Arrow) return;
		if(event.getDamager() instanceof Fireball) return;
		if(NonManager.getNon((LivingEntity) event.getDamager()) != null) return;

		for (NPC value : BossManager.clickables.values()) {
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
	public void onSpawn(SpawnerSpawnEvent event) {
		event.setCancelled(true);
	}

	public static void clearMobs() {
		main:
		for (Entity entity : Bukkit.getWorld("darkzone").getEntities()) {

			if(entity instanceof Player) continue;
			if(CitizensAPI.getNPCRegistry().isNPC(entity)) continue;

			if(entity.getUniqueId().equals(TaintedWell.textLine1.getUniqueId())) continue;
			if(entity.getUniqueId().equals(TaintedWell.textLine2.getUniqueId())) continue;
			if(entity.getUniqueId().equals(TaintedWell.textLine3.getUniqueId())) continue;
			if(entity.getUniqueId().equals(TaintedWell.textLine4.getUniqueId())) continue;

			if(entity instanceof Item) continue;
			if(entity instanceof Arrow) continue;
			if(entity instanceof Wither) continue;
			if(entity instanceof ThrownPotion) continue;
			if(entity instanceof Villager) continue;
			if(entity instanceof Fireball) continue;
			if(entity instanceof Slime && !(entity instanceof MagmaCube)) continue;

			if(entity.getUniqueId().equals(AuctionDisplays.timerStandUUID)) continue;

			if(entity instanceof ArmorStand && entity.getLocation().distance(AuctionManager.spawnLoc) < 50 && entity.getCustomName() == null) continue;

			if(entity.getUniqueId().equals(TaintedWell.wellStand.getUniqueId())) continue;

			for (PitMob mob : mobs) {
				if(mob.entity.getUniqueId().equals(entity.getUniqueId())) continue main;
				if(nameTags.get(mob.entity.getUniqueId()).getUniqueId().equals(entity.getUniqueId())) continue main;
			}
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
			for (ArmorStand value : CleaveSpell.stands.values()) {
				if(value.getUniqueId().equals(entity.getUniqueId())) continue main;
			}
			for (UUID pedestalArmorStand : AuctionDisplays.pedestalArmorStands) {
				if(pedestalArmorStand.equals(entity.getUniqueId())) continue main;
			}
			for (UUID pedestalArmorStand : AuctionDisplays.highestBidderStands) {
				if(pedestalArmorStand.equals(entity.getUniqueId())) continue main;
			}
			for (UUID pedestalArmorStand : AuctionDisplays.highestBidStands) {
				if(pedestalArmorStand.equals(entity.getUniqueId())) continue main;
			}
			for (UUID pedestalArmorStand : AuctionDisplays.rightClickStands) {
				if(pedestalArmorStand.equals(entity.getUniqueId())) continue main;
			}

			entity.remove();
		}
	}




}
