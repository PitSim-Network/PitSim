package dev.kyro.pitsim.adarkzone;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.notdarkzone.PitEquipment;
import dev.kyro.pitsim.adarkzone.sublevels.ZombieSubLevel;
import dev.kyro.pitsim.misc.Sounds;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class DarkzoneManager implements Listener {
	public static List<SubLevel> subLevels = new ArrayList<>();

	static {
		registerSubLevel(new ZombieSubLevel());

		new BukkitRunnable() {
			@Override
			public void run() {
				for(SubLevel subLevel : subLevels) subLevel.tick();
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 5);
	}


	/*
	 * Events:PlayerInteractEvent
	 * Description: Checks for PlayerInteractEvent and checks if all spawning conditions for the boss are met and spawns
	 * the boss in the corresponding sublevel. Also disables mobs in the sublevel.
	 */

	@EventHandler
	public void onClick(PlayerInteractEvent event) {

		if(event.getPlayer() == null) return;

		if(event.getItem() == null) return;
		ItemStack item = event.getItem();
		Location location = event.getClickedBlock().getLocation();

		for(SubLevel subLevel : subLevels) {
			if (subLevel.getSpawnItem().equals(item)) {
				if(subLevel.getMiddle().equals(location)) {
					subLevel.currentDrops++;

					if(subLevel.currentDrops >= subLevel.getRequiredDropsToSpawn()) {
						subLevel.getMiddle().getWorld().playEffect(subLevel.getMiddle(), Effect.EXPLOSION_HUGE, 100);
						Sounds.PRESTIGE.play(subLevel.getMiddle());
						subLevel.disableMobs();
						subLevel.spawnBoss(event.getPlayer());
						subLevel.currentDrops = 0;
					}
				}
				break;
			}
		}
	}


	/*
	 * Events:EntityDamageEvent
	 * Description: Cancels suffocation and fall damage.
	 */
	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if(event.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION || event.getCause() == EntityDamageEvent.DamageCause.FALL)
			event.setCancelled(true);
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


	/*
	 * Parameters: SubLevel subLevel
	 * Description: Registers a sublevel to the DarkzoneManager
	 */
	public static void registerSubLevel(SubLevel subLevel) {
		subLevels.add(subLevel);
	}

	public static SubLevel getSubLevel(Class<? extends SubLevel> clazz) {
		for(SubLevel subLevel : subLevels) if(subLevel.getClass() == clazz) return subLevel;
		throw new RuntimeException();
	}

	//method to remove all pitmobs from the subleve
}
