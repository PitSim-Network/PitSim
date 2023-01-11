package dev.kyro.pitsim.adarkzone;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.bosses.PitZombieBoss;
import dev.kyro.pitsim.adarkzone.mobs.PitZombie;
import dev.kyro.pitsim.adarkzone.notdarkzone.PitEquipment;
import dev.kyro.pitsim.aitems.mobdrops.RottenFlesh;
import dev.kyro.pitsim.controllers.ItemFactory;
import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.misc.Misc;
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

	static {
		SubLevel zombieSublevel = new SubLevel(
				SubLevelType.ZOMBIE, PitZombieBoss.class, PitZombie.class,
				new Location(MapManager.getDarkzone(), 327, 67, -143),
				20, 17, 12);
		ItemStack zombieSpawnItem = ItemFactory.getItem(RottenFlesh.class).getItem(1);
		zombieSublevel.setSpawnItem(zombieSpawnItem);
		zombieSublevel.addMobDrop(ItemFactory.getItem(RottenFlesh.class).getItem(1), 1);
		registerSubLevel(zombieSublevel);

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
		ItemStack heldStack = player.getItemInHand();
		Location location = event.getClickedBlock().getLocation();
		if(Misc.isAirOrNull(heldStack)) return;

		for(SubLevel subLevel : subLevels) {
			if(subLevel.isBossSpawned() || !subLevel.getSpawnItem().isSimilar(heldStack) || !subLevel.getMiddle().equals(location)) continue;

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
	 * Called when an entity is killed, checks if boss was killed, if so resets the sublevel to normal state and
	 * distrubutes rewards
	 * @param killEvent
	 */
	@EventHandler
	public static void onEntityDeath(KillEvent killEvent) {

		LivingEntity entity = killEvent.getDead();

		PitBoss killedBoss = BossManager.getPitBoss(entity);
		if(killedBoss != null) {
			killedBoss.kill();
			return;
		}

		Player killer = killEvent.getKillerPlayer();
		if (killer == null) {
			return;
		}

		for(SubLevel subLevel : subLevels) {
			if(subLevel.isBossSpawned()) continue;
			if(subLevel.isPitMob(entity)) {
				AUtil.giveItemSafely(killer, subLevel.getMobDropPool().getRandomDrop());
				subLevel.mobs.remove(entity);
			}
		}
	}

	public static PitEquipment getDefaultEquipment() {
		return new PitEquipment()
				.held(new ItemStack(Material.DIAMOND_SWORD))
				.helmet(new ItemStack(Material.DIAMOND_SWORD))
				.chestplate(new ItemStack(Material.DIAMOND_SWORD))
				.leggings(new ItemStack(Material.DIAMOND_SWORD))
				.leggings(new ItemStack(Material.DIAMOND_SWORD))
				.boots(new ItemStack(Material.DIAMOND_SWORD));
	}

	/**
	 * Adds a sublevel to the list of sublevels
	 * @param subLevel
	 */
	public static void registerSubLevel(SubLevel subLevel) {
		subLevels.add(subLevel);
	}

	/**
	 * Gets a sublevel by its type
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
}
