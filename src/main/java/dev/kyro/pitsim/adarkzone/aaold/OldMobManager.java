package dev.kyro.pitsim.adarkzone.aaold;

import de.myzelyam.api.vanish.VanishAPI;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.brewing.BrewingManager;
import dev.kyro.pitsim.controllers.*;
import dev.kyro.pitsim.controllers.objects.GoldenHelmet;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enchants.tainted.CleaveSpell;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.mobs.OldPitMagmaCube;
import dev.kyro.pitsim.mobs.OldPitSpiderBrute;
import dev.kyro.pitsim.mobs.OldPitStrongPigman;
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

public class OldMobManager implements Listener {
	public static List<OldPitMob> mobs = new ArrayList<>();
	public static Map<UUID, ArmorStand> nameTags = new HashMap<>();
	public static Map<ArmorStand, Location> locs = new HashMap<>();
	public static Map<ArmorStand, Location> oldLocs = new HashMap<>();

	public static final int MAX_TARGETS = 4;

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!attackEvent.isAttackerPlayer() || attackEvent.isDefenderPlayer()) return;
		if(!MapManager.inDarkzone(attackEvent.getAttackerPlayer().getLocation())) return;
		if(!(attackEvent.getDefender() instanceof Creature)) return;
		OldPitMob oldPitMob = OldPitMob.getPitMob(attackEvent.getDefender());
		if(oldPitMob == null) return;
		if(VanishAPI.isInvisible(attackEvent.getAttackerPlayer())) return;
		if(attackEvent.getAttackerPlayer().getGameMode() == GameMode.SURVIVAL) {
			((Creature) attackEvent.getDefender()).setTarget(attackEvent.getAttackerPlayer());
			oldPitMob.lastHit = System.currentTimeMillis();
			oldPitMob.target = attackEvent.getAttackerPlayer();
		}
	}

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				if(!PitSim.getStatus().isDarkzone()) return;

				for(Entity entity : new ArrayList<>(MapManager.getDarkzone().getEntities())) {

					if(!(entity instanceof LivingEntity)) continue;
					if(entity instanceof ArmorStand) continue;
					if(entity instanceof Player) continue;

					for(OldPitMob mob : new ArrayList<>(mobs)) {
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
				if(!PitSim.getStatus().isDarkzone()) return;

				clearMobs();
				for(SubLevel level : SubLevel.values()) {

					int currentMobs = 0;
					for(OldPitMob mob : mobs) {
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
					} catch(InstantiationException | IllegalAccessException | NoSuchMethodException |
							InvocationTargetException ignored) {
					}
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 40, 40);

		new BukkitRunnable() {
			@Override
			public void run() {
				if(!PitSim.getStatus().isDarkzone()) return;
				List<OldPitMob> toRemove = new ArrayList<>();
				for(OldPitMob mob : mobs) {

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

				for(OldPitMob oldPitMob : toRemove) {
					mobs.remove(oldPitMob);
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 20 * 20, 20 * 20);

		new BukkitRunnable() {
			@Override
			public void run() {
				if(!PitSim.getStatus().isDarkzone()) return;
				List<OldPitMob> toRemove = new ArrayList<>();
				for(OldPitMob mob : mobs) {
					if(mob.entity.isDead()) {
						nameTags.get(mob.entity.getUniqueId()).remove();
						toRemove.add(mob);
					}
				}
				for(OldPitMob oldPitMob : toRemove) {
					mobs.remove(oldPitMob);
				}
			}

		}.runTaskTimer(PitSim.INSTANCE, 20 * 5, 20 * 5);

		new BukkitRunnable() {
			@Override
			public void run() {
				if(!PitSim.getStatus().isDarkzone()) return;
				clearMobs();
			}
		}.runTaskLater(PitSim.INSTANCE, 10);

		new BukkitRunnable() {
			@Override
			public void run() {
				if(!PitSim.getStatus().isDarkzone()) return;
				for(OldPitMob mob : OldMobManager.mobs) {
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

					HashMap<OldPitMob, Double> noTarget = new HashMap<>();
					int targets = 0;

					List<OldPitMob> mobsCopy = new ArrayList<>(mobs);
					Collections.shuffle(mobsCopy);
					for(OldPitMob mob : mobsCopy) {
						if(mob instanceof OldPitStrongPigman || mob instanceof OldPitSpiderBrute || !(mob.entity instanceof Creature))
							continue;
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
					for(Map.Entry<OldPitMob, Double> entry : noTarget.entrySet()) {
						OldPitMob oldPitMob = entry.getKey();
						if(player.getGameMode() == GameMode.SURVIVAL && !VanishAPI.isInvisible(player)) {
							((Creature) oldPitMob.entity).setTarget(player);
							oldPitMob.lastHit = System.currentTimeMillis();
							oldPitMob.target = player;
							targets++;
						}
						if(targets >= MAX_TARGETS) break;
					}
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 15, 20);
	}

	public static boolean mobIsType(LivingEntity mob, Class... classes) {
		if(mob == null) return false;
		OldPitMob oldPitMob = OldPitMob.getPitMob(mob);
		if(oldPitMob == null) return false;
		for(Class clazz : classes) {
			if(oldPitMob.getClass() == clazz) return true;
		}
		return false;
	}

	// function to sort hashmap by values
	public static HashMap<OldPitMob, Double> sortByValue(HashMap<OldPitMob, Double> hm) {
		List<Map.Entry<OldPitMob, Double>> list =
				new LinkedList<>(hm.entrySet());
		list.sort(Map.Entry.comparingByValue());
		HashMap<OldPitMob, Double> temp = new LinkedHashMap<>();
		for(Map.Entry<OldPitMob, Double> aa : list) {
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
		if(event.isDeadPlayer()) return;
		clearMobs();
		List<OldPitMob> toRemove = new ArrayList<>();
		for(OldPitMob mob : mobs) {
			if(mob.entity.getUniqueId().equals(event.getDead().getUniqueId())) {
				for(Entity entity : Bukkit.getWorld("darkzone").getEntities()) {
					if(entity.getUniqueId().equals(nameTags.get(mob.entity.getUniqueId()).getUniqueId())) {
						entity.remove();
					}
				}
				toRemove.add(mob);


				ItemStack helmet = GoldenHelmet.getHelmet(event.getKillerPlayer());

				int level = 0;
				double chance = 0;
				if(helmet != null) level = HelmetSystem.getLevel(GoldenHelmet.getUsedHelmetGold(event.getDeadPlayer()));
				if(helmet != null)
					chance = HelmetSystem.Passive.DROP_CHANCE.baseUnit * HelmetSystem.getTotalStacks(HelmetSystem.Passive.DROP_CHANCE, level - 1);

				double multiplier = chance / 100.0 + 1;

				List<ItemStack> drops = shouldGiveDrop(mob, multiplier);

				for(ItemStack drop : drops) {
					PitPlayer pitPlayer = PitPlayer.getPitPlayer(event.getKillerPlayer());
					event.getDead().getWorld().dropItemNaturally(event.getDead().getLocation(), drop);
				}

//				for (Map.Entry<ItemStack, Integer> entry : drops.entrySet()) {
//					Random r = new Random();
//					int low = 1;
//					int high = 100;
//					int result = r.nextInt(high-low) + low;
//
//					result += result * (chance * 0.01);
//
//					if(result > entry.getValue()) continue;
//
//					PitPlayer pitPlayer = PitPlayer.getPitPlayer(event.killerPlayer);
//					if(pitPlayer.hasPerk(Telekinesis.INSTANCE)) {
//						AUtil.giveItemSafely(event.killerPlayer, entry.getKey());
//					} else event.dead.getWorld().dropItemNaturally(event.dead.getLocation(), entry.getKey());
//				}
			}
		}
		new BukkitRunnable() {
			@Override
			public void run() {
				for(OldPitMob oldPitMob : toRemove) {
					mobs.remove(oldPitMob);
				}
			}
		}.runTaskLater(PitSim.INSTANCE, 1);
	}

	public static List<ItemStack> shouldGiveDrop(OldPitMob oldPitMob, double multiplier) {
		List<ItemStack> drops = new ArrayList<>();

		for(Map.Entry<ItemStack, Integer> entry : oldPitMob.getDrops().entrySet()) {
			ItemStack drop = entry.getKey();
			double chance = entry.getValue();
			chance *= multiplier;

			if(chance < Math.random() * 100) continue;
			drops.add(drop);
		}

		return drops;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onHit(AttackEvent.Pre event) {

		if(event.getAttacker() instanceof MagmaCube && (!(event.getDefender() instanceof Player)))
			event.setCancelled(true);

		for(NPC value : OldBossManager.clickables.values()) {
			if(event.getDefender().getUniqueId().equals(value.getUniqueId())) event.setCancelled(true);
		}

		if(!(event.getDefender() instanceof ArmorStand)) return;

		for(ArmorStand value : nameTags.values()) {
			if(event.getDefender().getUniqueId().equals(value.getUniqueId())) event.setCancelled(true);
		}
	}

	@EventHandler
	public void onMobAttack(EntityDamageByEntityEvent event) {
		if(event.getDamager() instanceof Player) return;
		if(!(event.getDamager() instanceof LivingEntity)) return;
		OldPitMob mob = OldPitMob.getPitMob((LivingEntity) event.getDamager());
		if(mob == null) return;

		mob.lastHit = System.currentTimeMillis();

		if(mob instanceof OldPitMagmaCube) return;

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
		if(event.getReason() != EntityTargetEvent.TargetReason.CUSTOM && (!(event.getEntity() instanceof Slime)))
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onHit(EntityDamageByEntityEvent event) {
		if(event.getDamager() instanceof Arrow) return;
		if(event.getDamager() instanceof Fireball) return;
		if(NonManager.getNon((LivingEntity) event.getDamager()) != null) return;

		for(NPC value : OldBossManager.clickables.values()) {
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
		for(Entity entity : Bukkit.getWorld("darkzone").getEntities()) {

			if(entity instanceof Player) continue;
			if(CitizensAPI.getNPCRegistry().isNPC(entity)) continue;

			if(PitSim.getStatus().isDarkzone()) {
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

				if(entity instanceof ArmorStand && entity.getLocation().distance(AuctionManager.spawnLoc) < 50 && entity.getCustomName() == null)
					continue;

				if(entity.getUniqueId().equals(TaintedWell.wellStand.getUniqueId())) continue;

				for(OldPitMob mob : mobs) {
					if(mob.entity.getUniqueId().equals(entity.getUniqueId())) continue main;
					if(nameTags.get(mob.entity.getUniqueId()).getUniqueId().equals(entity.getUniqueId())) continue main;
				}
//			if(entity.getUniqueId().equals(TaintedWell.removeStand.getUniqueId())) continue;
				for(ArmorStand value : TaintedWell.enchantStands.values()) {
					if(value.getUniqueId().equals(entity.getUniqueId())) continue main;
				}
				for(ArmorStand value : TaintedWell.removeStands.values()) {
					if(value.getUniqueId().equals(entity.getUniqueId())) continue main;
				}
				for(ArmorStand value : BrewingManager.brewingStands) {
					if(value.getUniqueId().equals(entity.getUniqueId())) continue main;
				}
				for(ArmorStand value : CleaveSpell.standMap.values()) {
					if(value.getUniqueId().equals(entity.getUniqueId())) continue main;
				}
				for(UUID pedestalArmorStand : AuctionDisplays.pedestalArmorStands) {
					if(pedestalArmorStand != null && pedestalArmorStand.equals(entity.getUniqueId())) continue main;
				}
				for(UUID pedestalArmorStand : AuctionDisplays.highestBidderStands) {
					if(pedestalArmorStand != null && pedestalArmorStand.equals(entity.getUniqueId())) continue main;
				}
				for(UUID pedestalArmorStand : AuctionDisplays.highestBidStands) {
					if(pedestalArmorStand != null && pedestalArmorStand.equals(entity.getUniqueId())) continue main;
				}
				for(UUID pedestalArmorStand : AuctionDisplays.rightClickStands) {
					if(pedestalArmorStand != null && pedestalArmorStand.equals(entity.getUniqueId())) continue main;
				}
			}

			entity.remove();
		}
	}


}
