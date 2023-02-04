package dev.kyro.pitsim.adarkzone;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.bosses.PitZombieBoss;
import dev.kyro.pitsim.adarkzone.mobs.PitZombie;
import dev.kyro.pitsim.adarkzone.notdarkzone.PitEquipment;
import dev.kyro.pitsim.aitems.PitItem;
import dev.kyro.pitsim.aitems.mobdrops.RottenFlesh;
import dev.kyro.pitsim.controllers.ItemFactory;
import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.PlayerManager;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class DarkzoneManager implements Listener {
	public static List<SubLevel> subLevels = new ArrayList<>();
	public static List<Hologram> holograms = new ArrayList<>();

	public DarkzoneManager() {
		SubLevel zombieSubLevel = new SubLevel(
				SubLevelType.ZOMBIE, PitZombieBoss.class, PitZombie.class,
				new Location(MapManager.getDarkzone(), 327, 67, -143),
				20, 17, 12);
		zombieSubLevel.setSpawnItemClass(RottenFlesh.class);
		registerSubLevel(zombieSubLevel);

		for(SubLevel subLevel : subLevels) subLevel.init();
		new BukkitRunnable() {
			@Override
			public void run() {
				for(SubLevel subLevel : subLevels) subLevel.tick();
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 5);
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
