package dev.kyro.pitsim.adarkzone;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.bosses.PitZombieBoss;
import dev.kyro.pitsim.adarkzone.mobs.PitZombie;
import dev.kyro.pitsim.adarkzone.notdarkzone.PitEquipment;
import dev.kyro.pitsim.brewing.ingredients.RottenFlesh;
import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
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
		ItemStack zombieSpawnItem = RottenFlesh.INSTANCE.getItem();
		zombieSublevel.setSpawnItem(zombieSpawnItem);

		registerSubLevel(zombieSublevel);

		for(Hologram hologram : HologramsAPI.getHolograms(PitSim.INSTANCE)) {
			hologram.delete();
		}

		Hologram zombieHologram = HologramsAPI.createHologram(PitSim.INSTANCE, zombieSublevel.getMiddle().add(0.5, 1.6, 0.5));
		zombieHologram.setAllowPlaceholders(true);
		zombieHologram.appendTextLine(ChatColor.RED + "Place " + ChatColor.translateAlternateColorCodes('&', "&aRotten Flesh"));
		zombieHologram.appendTextLine("{fast}" + "%pitsim_zombiecave%" + " ");
		holograms.add(zombieHologram);

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

		if(event.getPlayer() == null) return;
		if(event.getPlayer().getItemInHand() == null) return;
		if(event.getAction() != Action.LEFT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;



		ItemStack item = event.getItem();
		Location location = event.getClickedBlock().getLocation();

		for(SubLevel subLevel : subLevels) {
			if(subLevel.isBossSpawned()) continue;
			if (subLevel.getSpawnItem() == null) {
				continue;
			}
			if (subLevel.getSpawnItem().isSimilar(item)) {
				if(subLevel.getMiddle().equals(location)) {
					subLevel.setCurrentDrops(subLevel.getCurrentDrops() + 1);
					item.setAmount(item.getAmount() - 1);
					if(item.getAmount() == 1) {
						event.getPlayer().setItemInHand(null);
					}

					System.out.println("Current drops: " + subLevel.getCurrentDrops());

					if(subLevel.getCurrentDrops() >= subLevel.getRequiredDropsToSpawn()) {
						subLevel.getMiddle().getWorld().playEffect(subLevel.getMiddle(), Effect.EXPLOSION_HUGE, 100);
						Sounds.PRESTIGE.play(subLevel.getMiddle());
						subLevel.disableMobs();
						subLevel.spawnBoss(event.getPlayer());

						subLevel.setCurrentDrops(0);
						//decrese the item stack in the players hand by 1

					}
				}
			}
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
	public static SubLevel getSublevel(SubLevelType type) {
		for(SubLevel subLevel : subLevels) {
			if(subLevel.getSubLevelType() == type) {
				return subLevel;
			}
		}
		return null;
	}
}
