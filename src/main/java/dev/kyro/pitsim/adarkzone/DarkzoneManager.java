package dev.kyro.pitsim.adarkzone;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.bosses.PitZombieBoss;
import dev.kyro.pitsim.adarkzone.mobs.*;
import dev.kyro.pitsim.adarkzone.notdarkzone.PitEquipment;
import dev.kyro.pitsim.aitems.PitItem;
import dev.kyro.pitsim.aitems.misc.SoulPickup;
import dev.kyro.pitsim.aitems.mobdrops.EnderPearl;
import dev.kyro.pitsim.aitems.mobdrops.*;
import dev.kyro.pitsim.controllers.ItemFactory;
import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.PlayerManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.events.ManaRegenEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class DarkzoneManager implements Listener {
	public static List<SubLevel> subLevels = new ArrayList<>();
	public static List<Hologram> holograms = new ArrayList<>();

	public DarkzoneManager() {
		SubLevel subLevel;

		subLevel = new SubLevel(
				SubLevelType.ZOMBIE, PitZombieBoss.class, PitZombie.class, EntityType.ZOMBIE, RottenFlesh.class,
				new Location(MapManager.getDarkzone(), 327, 67, -143),
				15, 17, 12);
		registerSubLevel(subLevel);

		subLevel = new SubLevel(
				SubLevelType.SKELETON, PitZombieBoss.class, PitSkeleton.class, EntityType.SKELETON, Bone.class,
				new Location(MapManager.getDarkzone(), 424, 52, -128),
				15, 17, 12);
		registerSubLevel(subLevel);

		subLevel = new SubLevel(
				SubLevelType.SPIDER, PitZombieBoss.class, PitSpider.class, EntityType.SPIDER, SpiderEye.class,
				new Location(MapManager.getDarkzone(), 463, 37, -72),
				15, 17, 12);
		registerSubLevel(subLevel);

		subLevel = new SubLevel(
				SubLevelType.WOLF, PitZombieBoss.class, PitWolf.class, EntityType.WOLF, Leather.class,
				new Location(MapManager.getDarkzone(), 419, 25, -27),
				15, 17, 12);
		registerSubLevel(subLevel);

		subLevel = new SubLevel(
				SubLevelType.BLAZE, PitZombieBoss.class, PitBlaze.class, EntityType.BLAZE, BlazeRod.class,
				new Location(MapManager.getDarkzone(), 342, 19, 15),
				15, 17, 12);
		registerSubLevel(subLevel);

		subLevel = new SubLevel(
				SubLevelType.ZOMBIE_PIGMAN, PitZombieBoss.class, PitZombiePigman.class, EntityType.PIG_ZOMBIE, RawPork.class,
				new Location(MapManager.getDarkzone(), 235, 19, -23),
				15, 17, 12);
		registerSubLevel(subLevel);

		subLevel = new SubLevel(
				SubLevelType.WITHER_SKELETON, PitZombieBoss.class, PitWitherSkeleton.class, EntityType.SKELETON, Charcoal.class,
				new Location(MapManager.getDarkzone(), 210, 19, -115),
				15, 17, 12);
		registerSubLevel(subLevel);

		subLevel = new SubLevel(
				SubLevelType.CREEPER, PitZombieBoss.class, PitCreeper.class, EntityType.CREEPER, Gunpowder.class,
				new Location(MapManager.getDarkzone(), 247, 20, -174),
				15, 17, 12);
		subLevel.mobTargetingSystem.persistenceWeight *= 2;
		subLevel.mobTargetingSystem.otherMobsTargetingWeight *= 1.5;
		registerSubLevel(subLevel);

		subLevel = new SubLevel(
				SubLevelType.IRON_GOLEM, PitZombieBoss.class, PitIronGolem.class, EntityType.IRON_GOLEM, IronIngot.class,
				new Location(MapManager.getDarkzone(), 313, 19, -217),
				15, 17, 12);
		registerSubLevel(subLevel);

		subLevel = new SubLevel(
				SubLevelType.ENDERMAN, PitZombieBoss.class, PitEnderman.class, EntityType.ENDERMAN, EnderPearl.class,
				new Location(MapManager.getDarkzone(), 388, 19, -226),
				15, 17, 12);
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

					ManaRegenEvent event = new ManaRegenEvent(player, 5);
					Bukkit.getPluginManager().callEvent(event);
					if(!event.isCancelled()) {
						double mana = event.getFinalMana();
						if(pitPlayer.mana + mana <= pitPlayer.getMaxMana()) pitPlayer.mana += mana;
					}

					pitPlayer.updateManaBar();
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 30L);
	}

	@EventHandler
	public void onSpawn(CreatureSpawnEvent event) {
		if(event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL || event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.REINFORCEMENTS)
			event.setCancelled(true);
	}

	@EventHandler
	public void onChunkUnload(ChunkUnloadEvent event) {
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
	public void onTarget(EntityTargetLivingEntityEvent event) {
		if(!(event.getEntity() instanceof LivingEntity) || event.getReason() == EntityTargetEvent.TargetReason.TARGET_ATTACKED_ENTITY) return;
		LivingEntity entity = (LivingEntity) event.getEntity();
		if(!isPitMob(entity)) return;
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

			subLevel.setCurrentDrops(subLevel.getCurrentDrops() + 1);
			if(heldStack.getAmount() == 1) {
				player.setItemInHand(null);
			} else {
				heldStack.setAmount(heldStack.getAmount() - 1);
			}
			player.updateInventory();

			if(subLevel.getCurrentDrops() < subLevel.getRequiredDropsToSpawn()) continue;

			subLevel.getMiddle().getWorld().playEffect(subLevel.getMiddle(), Effect.EXPLOSION_HUGE, 100);
			Sounds.PRESTIGE.play(subLevel.getMiddle());
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
	@EventHandler
	public static void onEntityDeath(KillEvent killEvent) {

		PitBoss deadBoss = BossManager.getPitBoss(killEvent.getDead());
		if(deadBoss != null) {
			if(!PlayerManager.isRealPlayer(killEvent.getKillerPlayer())) throw new RuntimeException();
			deadBoss.kill(killEvent.getKillerPlayer());
			return;
		}

		PitMob deadMob = getPitMob(killEvent.getDead());
		if(deadMob != null) {
			if(!PlayerManager.isRealPlayer(killEvent.getKillerPlayer())) throw new RuntimeException();
			deadMob.kill(killEvent.getKillerPlayer());
			return;
		}
	}

	public static void createSoulExplosion(Location location, int souls) {
		int items = (int) Math.sqrt(souls);
		Map<Integer, Integer> soulDistributionMap = new HashMap<>();
		int soulsToDistribute = souls - items;
		for(int i = 0; i < items; i++) soulDistributionMap.put(i, 1);
		for(int i = 0; i < soulsToDistribute; i++) {
			int randomStack = new Random().nextInt(items);
			soulDistributionMap.put(randomStack, soulDistributionMap.get(randomStack) + 1);
		}
		for(Map.Entry<Integer, Integer> entry : soulDistributionMap.entrySet()) {
			Location spawnLocation = location.clone().add(Misc.randomOffset(2), Misc.randomOffsetPositive(2), Misc.randomOffset(2));
			ItemStack soulStack = ItemFactory.getItem(SoulPickup.class).getItem(entry.getValue());

			Item droppedItem = location.getWorld().dropItem(spawnLocation, soulStack);
			new BukkitRunnable() {
				@Override
				public void run() {
					if(droppedItem.isValid()) droppedItem.remove();
				}
			}.runTaskLater(PitSim.INSTANCE, 100 + new Random().nextInt(101));

			Vector velocityVector = spawnLocation.toVector().subtract(location.toVector()).normalize().multiply(0.4);
			double multiplier = Math.random() * 0.5 + 0.75;
			droppedItem.setVelocity(velocityVector.multiply(multiplier));
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
}
