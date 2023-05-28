package dev.kyro.pitsim.adarkzone;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.bosses.*;
import dev.kyro.pitsim.adarkzone.mobs.*;
import dev.kyro.pitsim.adarkzone.notdarkzone.PitEquipment;
import dev.kyro.pitsim.adarkzone.progression.ProgressionManager;
import dev.kyro.pitsim.adarkzone.progression.SkillBranch;
import dev.kyro.pitsim.adarkzone.progression.skillbranches.DamageBranch;
import dev.kyro.pitsim.aitems.PitItem;
import dev.kyro.pitsim.aitems.misc.SoulPickup;
import dev.kyro.pitsim.aitems.mobdrops.EnderPearl;
import dev.kyro.pitsim.aitems.mobdrops.*;
import dev.kyro.pitsim.controllers.*;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitEnchantSpell;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enchants.tainted.chestplate.Resilient;
import dev.kyro.pitsim.enums.MobStatus;
import dev.kyro.pitsim.enums.MysticType;
import dev.kyro.pitsim.events.*;
import dev.kyro.pitsim.holograms.Hologram;
import dev.kyro.pitsim.holograms.RefreshMode;
import dev.kyro.pitsim.holograms.ViewMode;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import dev.kyro.pitsim.misc.effects.SelectiveDrop;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.lang.reflect.Constructor;
import java.util.*;

public class DarkzoneManager implements Listener {
	public static List<SubLevel> subLevels = new ArrayList<>();
	public static List<Chunk> mapChunks = new ArrayList<>();

	public static List<Player> regenCooldownList = new ArrayList<>();
	public static List<UUID> freshSoftCooldownList = new ArrayList<>();
	public static Map<UUID, Integer> mobKillTrackMapMap = new HashMap<>();
	public static List<Player> extraKillsNotificationCooldown = new ArrayList<>();

	public DarkzoneManager() {
		SubLevel subLevel;

		subLevel = new SubLevel(
				SubLevelType.ZOMBIE, PitZombieBoss.class, PitZombie.class, EntityType.ZOMBIE, RottenFlesh.class,
				new Location(MapManager.getDarkzone(), 327, 67, -143),
				20, 23, 10, -1,
				new Location(MapManager.getDarkzone(), 302.5, 74, -133.5, -120, 0));
		registerSubLevel(subLevel);

		subLevel = new SubLevel(
				SubLevelType.SKELETON, PitSkeletonBoss.class, PitSkeleton.class, EntityType.SKELETON, Bone.class,
				new Location(MapManager.getDarkzone(), 424, 52, -128),
				19, 21, 10, -1,
				new Location(MapManager.getDarkzone(), 398.5, 54, -125.5, -90, 0));
		registerSubLevel(subLevel);

		subLevel = new SubLevel(
				SubLevelType.SPIDER, PitSpiderBoss.class, PitSpider.class, EntityType.SPIDER, SpiderEye.class,
				new Location(MapManager.getDarkzone(), 463, 37, -72),
				18, 19, 10, -1,
				new Location(MapManager.getDarkzone(), 453.5, 40, -94.5, -30, 0));
		registerSubLevel(subLevel);

		subLevel = new SubLevel(
				SubLevelType.WOLF, PitWolfBoss.class, PitWolf.class, EntityType.WOLF, Leather.class,
				new Location(MapManager.getDarkzone(), 397, 28, -42),
				17, 22, 10, 50,
				new Location(MapManager.getDarkzone(), 421.5, 28, -32.5, 90, 0));
		registerSubLevel(subLevel);

		subLevel = new SubLevel(
				SubLevelType.BLAZE, PitBlazeBoss.class, PitBlaze.class, EntityType.BLAZE, BlazeRod.class,
				new Location(MapManager.getDarkzone(), 314, 22, -19),
				16, 20, 10, -1,
				new Location(MapManager.getDarkzone(), 336.5, 22, -25.5, 70, 0));
		registerSubLevel(subLevel);

		subLevel = new SubLevel(
				SubLevelType.ZOMBIE_PIGMAN, PitZombiePigmanBoss.class, PitZombiePigman.class, EntityType.PIG_ZOMBIE, RawPork.class,
				new Location(MapManager.getDarkzone(), 235, 19, -23),
				15, 21, 10, 10,
				new Location(MapManager.getDarkzone(), 260.5, 19, -13.5, 110, 0));
		registerSubLevel(subLevel);

		subLevel = new SubLevel(
				SubLevelType.WITHER_SKELETON, PitWitherSkeletonBoss.class, PitWitherSkeleton.class, EntityType.SKELETON, Charcoal.class,
				new Location(MapManager.getDarkzone(), 210, 19, -115),
				15, 21, 10, -1,
				new Location(MapManager.getDarkzone(), 219.5, 18, -90.5, 180, 0));
		registerSubLevel(subLevel);

		subLevel = new SubLevel(
				SubLevelType.CREEPER, PitCreeperBoss.class, PitCreeper.class, EntityType.CREEPER, Gunpowder.class,
				new Location(MapManager.getDarkzone(), 256, 18, -172),
				15, 20, 10, -1,
				new Location(MapManager.getDarkzone(), 239.5, 19, -151.5, -145, 0));
		subLevel.mobTargetingSystem.persistenceWeight *= 2;
		subLevel.mobTargetingSystem.otherMobsTargetingWeight *= 1.5;
		registerSubLevel(subLevel);

		subLevel = new SubLevel(
				SubLevelType.IRON_GOLEM, PitIronGolemBoss.class, PitIronGolem.class, EntityType.IRON_GOLEM, IronIngot.class,
				new Location(MapManager.getDarkzone(), 313, 19, -217),
				12, 20, 10, -1,
				new Location(MapManager.getDarkzone(), 291.5, 19, -196.5, -130, 0));
		registerSubLevel(subLevel);

		subLevel = new SubLevel(
				SubLevelType.ENDERMAN, PitEndermanBoss.class, PitEnderman.class, EntityType.ENDERMAN, EnderPearl.class,
				new Location(MapManager.getDarkzone(), 388, 19, -226),
				15, 21, 10, -1,
				new Location(MapManager.getDarkzone(), 361.5, 19, -225.5, -90, 0));
		registerSubLevel(subLevel);

		subLevels.forEach(SubLevel::init);
		new BukkitRunnable() {
			@Override
			public void run() {
				subLevels.forEach(SubLevel::tick);
				SubLevel.tick++;
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 5);

		new BukkitRunnable() {
			@Override
			public void run() {
				for(Player player : MapManager.getDarkzone().getPlayers()) {
					PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
					if(!pitPlayer.hasManaUnlocked()) continue;

					ManaRegenEvent event = new ManaRegenEvent(player, 0.07);
					Bukkit.getPluginManager().callEvent(event);
					if(!event.isCancelled()) {
						double mana = event.getFinalMana();
						pitPlayer.giveMana(mana);
					}

					if(!SpawnManager.isInSpawn(player)) pitPlayer.updateManaBar();
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 1L);

		new BukkitRunnable() {
			@Override
			public void run() {
				for(Player player : Bukkit.getOnlinePlayers()) {
					if(regenCooldownList.contains(player)) continue;
					PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
					pitPlayer.heal(1);

					int regenCooldownTicks = 30;
					regenCooldownTicks /= 1 + (Resilient.getRegenIncrease(pitPlayer) / 100.0);

					regenCooldownList.add(player);
					new BukkitRunnable() {
						@Override
						public void run() {
							regenCooldownList.remove(player);
						}
					}.runTaskLater(PitSim.INSTANCE, regenCooldownTicks);
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 1L);

		FastTravelManager.init();
	}

	@EventHandler
	public void onJoin(PitJoinEvent event) {
		Player player = event.getPlayer();
		putOnSoftFreshCooldown(player);
	}

	public static void putOnSoftFreshCooldown(Player player) {
		if(freshSoftCooldownList.contains(player.getUniqueId())) return;
		freshSoftCooldownList.add(player.getUniqueId());
		new BukkitRunnable() {
			@Override
			public void run() {
				freshSoftCooldownList.remove(player.getUniqueId());
			}
		}.runTaskLater(PitSim.INSTANCE, new Random().nextInt(20 * 60 * 2) + 20 * 60 * 3 + 1);
	}

	public static double getSoulSoftCooldownMultiplier(Player player) {
		int recentKills = mobKillTrackMapMap.getOrDefault(player.getUniqueId(), 0);
		int extraKills = Math.max(recentKills - DarkzoneBalancing.ALLOWED_KILLS_PER_MINUTE, 0);
		if(extraKills != 0 && !extraKillsNotificationCooldown.contains(player)) {
			extraKillsNotificationCooldown.add(player);
			new BukkitRunnable() {
				@Override
				public void run() {
					extraKillsNotificationCooldown.remove(player);
				}
			}.runTaskLater(PitSim.INSTANCE, 10 * 20L);

			AOutput.error(player, "&c&lWOAH!&7 You are killing mobs too quickly! Your chance of finding souls has been temporarily reduced!");
		}
		return Math.pow(0.9, extraKills);
	}

	@EventHandler
	public static void onMobKill(KillEvent killEvent) {
		if(!killEvent.isKillerRealPlayer()) return;
		PitMob pitMob = getPitMob(killEvent.getDead());
		if(pitMob == null || pitMob instanceof PitEnderman) return;

		mobKillTrackMapMap.put(killEvent.getKiller().getUniqueId(), mobKillTrackMapMap.getOrDefault(killEvent.getKiller().getUniqueId(), 0) + 1);
		new BukkitRunnable() {
			@Override
			public void run() {
				mobKillTrackMapMap.put(killEvent.getKiller().getUniqueId(), mobKillTrackMapMap.get(killEvent.getKiller().getUniqueId()) - 1);
			}
		}.runTaskLater(PitSim.INSTANCE, 20 * 60);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onClick2(PlayerInteractEvent event) {
		if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		Player player = event.getPlayer();
		if(!MapManager.inDarkzone(player)) return;
		ItemStack itemStack = player.getItemInHand();
		if(Misc.isAirOrNull(itemStack)) return;
		MysticType mysticType = MysticType.getMysticType(itemStack);
		if(mysticType != MysticType.TAINTED_SCYTHE) return;

		if(SpawnManager.isInSpawn(player)) {
			AOutput.send(player, "&c&c&lERROR!&7 You cannot use spells in the spawn area!");
			Sounds.NO.play(player);
			return;
		}

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		for(Map.Entry<PitEnchant, Integer> entry : new ArrayList<>(EnchantManager.getEnchantsOnPlayer(player).entrySet())) {
			PitEnchant pitEnchant = entry.getKey();
			int enchantLvl = entry.getValue();
			if(!(pitEnchant instanceof PitEnchantSpell)) continue;
			PitEnchantSpell pitSpell = (PitEnchantSpell) pitEnchant;
			int manaCost = pitSpell.getManaCost(enchantLvl);

			Cooldown cooldown = pitSpell.getCooldown(player, pitSpell.getCooldownTicks(enchantLvl));
			if(cooldown.isOnCooldown()) continue;
			if(pitPlayer.getMana() < manaCost) {
				Sounds.NO.play(player);
				continue;
			}

			if(pitSpell.onlyInSubLevels) {
				SubLevel subLevel = null;
				for(SubLevel testLevel : DarkzoneManager.subLevels) {
					double distance = testLevel.getMiddle().distance(player.getLocation());
					if(distance > testLevel.spawnRadius) continue;
					subLevel = testLevel;
					break;
				}
				if(subLevel == null) {
					AOutput.error(player, "&c&lERROR!&7 You have to be in a sub level to use " +
							pitSpell.getDisplayName(false, true));
					continue;
				}
			}

			SpellUseEvent spellUseEvent = new SpellUseEvent(player, pitSpell, enchantLvl);
			Bukkit.getPluginManager().callEvent(spellUseEvent);

			if(spellUseEvent.isCancelled()) {
				Sounds.NO.play(player);
				continue;
			}

			pitPlayer.useManaForSpell(manaCost);
			cooldown.restart();
		}
	}

	@EventHandler
	public void onAttack(AttackEvent.Pre attackEvent) {
		if(attackEvent.getWrapperEvent().getSpigotEvent().getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK &&
				attackEvent.getWrapperEvent().getSpigotEvent().getCause() != EntityDamageEvent.DamageCause.PROJECTILE) return;
		if(attackEvent.getWrapperEvent().hasAttackInfo()) return;
		PitMob pitMob = getPitMob(attackEvent.getAttacker());
		if(pitMob != null) attackEvent.getWrapperEvent().getSpigotEvent().setDamage(pitMob.getDamage());
	}

	@EventHandler
	public void onRegen(EntityRegainHealthEvent event) {
		if(!PitSim.status.isDarkzone() || !(event.getEntity() instanceof Player) ||
				event.getRegainReason() != EntityRegainHealthEvent.RegainReason.SATIATED) return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onSpawn(CreatureSpawnEvent event) {
		if(event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL || event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.REINFORCEMENTS)
			event.setCancelled(true);
	}

	@EventHandler
	public void onChunkUnload(ChunkUnloadEvent event) {

		if(mapChunks.contains(event.getChunk())) {
			event.setCancelled(true);
			return;
		}

		for(Entity entity : event.getChunk().getEntities()) {
			if(!(entity instanceof LivingEntity)) continue;
			LivingEntity livingEntity = (LivingEntity) entity;
			PitMob pitMob = DarkzoneManager.getPitMob(livingEntity);
			if(pitMob == null) continue;
			pitMob.remove();
		}
	}

	@EventHandler
	public void onSpawn(SpawnerSpawnEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!attackEvent.isAttackerRealPlayer()) return;

		PitMob pitMob = getPitMob(attackEvent.getDefender());
		if(pitMob == null) return;
		pitMob.setTarget(attackEvent.getAttackerPlayer());
	}

	@EventHandler
	public void onTarget(EntityTargetLivingEntityEvent event) {
		if(!(event.getEntity() instanceof LivingEntity)) return;
		LivingEntity entity = (LivingEntity) event.getEntity();
		if(!isPitMob(entity)) return;

//		if(event.getTarget() instanceof Player) {
//			Player target = (Player) event.getTarget();
//			if(event.getReason() == EntityTargetEvent.TargetReason.TARGET_ATTACKED_ENTITY && !Fearmonger.isImmune(target)) return;
//		}

		event.setCancelled(true);
	}

	/**
	 * Called when a player interacts with a block, checks if all the spawn conditions are met for a boss to
	 * spawn, and if so, spawns it.
	 * @param event
	 */
	@EventHandler
	public void onClick(PlayerInteractEvent event) {
		if(event.getAction() != Action.LEFT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

		Player player = event.getPlayer();
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		Location location = event.getClickedBlock().getLocation();

		ItemStack heldStack = player.getItemInHand();
		PitItem pitItem = ItemFactory.getItem(heldStack);
		if(pitItem == null) return;

		for(SubLevel subLevel : subLevels) {
			if(subLevel.getSpawnItemClass() != pitItem.getClass() || !subLevel.getMiddle().equals(location)) continue;

			if(subLevel.isBossSpawned()) {
				AOutput.error(player, "&c&lERROR!&7 You cannot do that while a boss is spawned");
				return;
			}

			int getEffectiveAmount = 1;
			if(ProgressionManager.isUnlocked(pitPlayer, DamageBranch.INSTANCE, SkillBranch.MajorUnlockPosition.SECOND_PATH) &&
					Math.random() < DamageBranch.getSecondItemSpawnChance() / 100.0) getEffectiveAmount *= 2;

			subLevel.setCurrentDrops(subLevel.getCurrentDrops() + getEffectiveAmount);
			if(heldStack.getAmount() == 1) {
				player.setItemInHand(null);
			} else {
				heldStack.setAmount(heldStack.getAmount() - 1);
			}
			player.updateInventory();

			if(subLevel.getCurrentDrops() < subLevel.getRequiredDropsToSpawn()) continue;

			subLevel.getMiddle().getWorld().playEffect(subLevel.getMiddle(), Effect.EXPLOSION_HUGE, 100);
			Sounds.BOSS_SPAWN.play(subLevel.getMiddle());
			subLevel.spawnBoss(player);

			subLevel.setCurrentDrops(0);
		}
	}

	/**
	 * Cancels all suffocation and fall damage in the darkzone
	 * @param event
	 **/
	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if(event.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION || event.getCause() == EntityDamageEvent.DamageCause.FALL)
			event.setCancelled(true);
	}

	/**
	 * Called when an entity is killed, checks if boss was killed, if so resets the subLevel to normal state and
	 * distrubutes rewards
	 * @param killEvent
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public static void onEntityDeath(KillEvent killEvent) {

		PitBoss deadBoss = BossManager.getPitBoss(killEvent.getDead());
		if(deadBoss != null) {
			if(!killEvent.hasKiller()) {
				deadBoss.kill(null);
				return;
			}
			if(!killEvent.isKillerRealPlayer()) throw new RuntimeException();
			deadBoss.kill(killEvent.getKillerPlayer());
			return;
		}

		PitMob deadMob = getPitMob(killEvent.getDead());
		if(deadMob != null) {
			if(!killEvent.hasKiller()) {
				deadMob.remove();
				return;
			}
			if(!killEvent.isKillerRealPlayer()) throw new RuntimeException();
			deadMob.kill(killEvent.getKillerPitPlayer());
			return;
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public static void onEntityDamage(EntityDamageEvent event) {
		if(!(event.getEntity() instanceof LivingEntity)) return;
		LivingEntity entity = (LivingEntity) event.getEntity();
		PitMob pitMob = DarkzoneManager.getPitMob(entity);
		if(pitMob == null) return;
		if(event.getFinalDamage() < entity.getHealth()) return;
		event.setCancelled(true);
		pitMob.kill(null);
	}

	public static void createSoulExplosion(LinkedHashMap<UUID, Double> damageMap, Location location, int souls, boolean largeExplosion) {
		for(Integer stackSize : Misc.createDistribution(souls, 3.0 / 5.0)) {
			Location spawnLocation = location.clone().add(Misc.randomOffset(2), Misc.randomOffsetPositive(2), Misc.randomOffset(2));
			ItemStack soulStack = ItemFactory.getItem(SoulPickup.class).getItem(stackSize);
			SelectiveDrop drop = new SelectiveDrop(soulStack, spawnLocation);

			Hologram hologram = new Hologram(spawnLocation, ViewMode.SELECT, RefreshMode.MANUAL) {
				@Override
				public List<String> getStrings(Player player) {
					int amount = drop.getItemStack().getAmount();
					return Collections.singletonList("&f" + amount + "x Soul" + Misc.s(amount));
				}
			};

			drop.setCallBack(new BukkitRunnable() {
				@Override
				public void run() {
					hologram.remove();
				}
			});

			if(damageMap != null) {
				UUID choice = Misc.weightedRandom(damageMap);
				Player player = Bukkit.getPlayer(choice);
				if(player == null) continue;
				drop.addPlayer(player);
			} else {
				for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
					drop.addPlayer(onlinePlayer);
				}
			}

			drop.dropItem();

			new BukkitRunnable() {
				@Override
				public void run() {
					drop.cleanUp();
				}
			}.runTaskLater(PitSim.INSTANCE, 20 * 20 + new Random().nextInt(20 * 10 + 1));

			Vector velocityVector = spawnLocation.toVector().subtract(location.toVector()).normalize().multiply(0.4);
			double multiplier = Math.random() * 0.5 + 0.75;
			if(largeExplosion) multiplier *= 1.8;
			drop.getDroppedItem().setVelocity(velocityVector.multiply(multiplier));

			new BukkitRunnable() {
				@Override
				public void run() {
					if(drop.getDroppedItem().isDead()) {
						hologram.remove();
						return;
					}

					hologram.teleport(drop.getDroppedItem().getLocation().add(0, 0.5, 0));

					for(UUID permittedPlayer : drop.getPermittedPlayers()) {
						Player player = Bukkit.getPlayer(permittedPlayer);
						if(player == null) continue;
						hologram.addPermittedViewer(player);
					}
				}
			}.runTaskLater(PitSim.INSTANCE, 25);

		}
		location.getWorld().playEffect(location, Effect.EXPLOSION_LARGE, 1);
		Sounds.SOUL_EXPLOSION.play(location);
	}

	public static PitEquipment getDefaultEquipment() {
		return new PitEquipment()
				.held(new ItemStack(Material.DIAMOND_SWORD))
				.helmet(new ItemStack(Material.DIAMOND_HELMET))
				.chestplate(new ItemStack(Material.DIAMOND_CHESTPLATE))
				.leggings(new ItemStack(Material.DIAMOND_LEGGINGS))
				.boots(new ItemStack(Material.DIAMOND_BOOTS));
	}

	/**
	 * Adds a subLevel to the list of subLevel
	 * @param subLevel
	 */
	public static void registerSubLevel(SubLevel subLevel) {
		subLevels.add(subLevel);
	}

	/**
	 * Gets a subLevel by its type
	 * @param type
	 * @return SubLevel
	 */
	public static SubLevel getSubLevel(SubLevelType type) {
		for(SubLevel subLevel : subLevels) {
			if(subLevel.getSubLevelType() == type) {
				return subLevel;
			}
		}
		return null;
	}

	public static SubLevel getSubLevel(String identifier) {
		for(SubLevel subLevel : subLevels) if(subLevel.getIdentifier().equalsIgnoreCase(identifier)) return subLevel;
		return null;
	}

	public static boolean isPitMob(LivingEntity entity) {
		return getPitMob(entity) != null;
	}

	public static boolean isPitMob(LivingEntity entity, SubLevel subLevel) {
		return getPitMob(entity, subLevel) != null;
	}

	public static PitMob getPitMob(LivingEntity entity) {
		return getPitMob(entity, null);
	}

	public static PitMob getPitMob(LivingEntity entity, SubLevel subLevel) {
		if(entity == null) return null;
		for(SubLevel testLevel : subLevels) {
			if(subLevel != null && testLevel != subLevel) continue;
			for(PitMob pitMob : testLevel.mobs) if(pitMob.getMob() == entity) return pitMob;
		}
		return null;
	}

	public static void loadChunks() {
		mapChunks.clear();
		Location middle = new Location(MapManager.getDarkzone(), 372, 0, -88);
		for(int x = -250; x < 250; x+= 16) {
			for(int z = -250; z < 250; z+= 16) {
				Location chunkLoc = middle.clone().add(x, 0, z);
				mapChunks.add(chunkLoc.getChunk());
			}
		}

		Location auctionRoom = new Location(MapManager.getDarkzone(), 178, 0, -1009);

		for(int i = -64; i < 64; i++) {
			Location chunkLoc = auctionRoom.clone().add(i, 0, i);
			mapChunks.add(chunkLoc.getChunk());
		}

		for(Chunk mapChunk : mapChunks) {
			mapChunk.load();
		}
	}

	public static void clearEntities() {

		for(Entity entity : MapManager.getDarkzone().getEntities()) {
			if(entity instanceof Player) continue;
			if(entity instanceof ItemFrame) continue;
			entity.remove();
		}
	}

	public static PitMob getDummyMob(Class<? extends PitMob> mobClass) {
		try {
			Constructor<? extends PitMob> constructor = mobClass.getConstructor(Location.class, MobStatus.class);
			return constructor.newInstance(null, MobStatus.STANDARD);
		} catch(Exception exception) {
			exception.printStackTrace();
			throw new RuntimeException();
		}
	}
}
