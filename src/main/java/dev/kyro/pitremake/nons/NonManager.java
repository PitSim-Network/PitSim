package dev.kyro.pitremake.nons;

import dev.kyro.pitremake.PitRemake;
import dev.kyro.pitremake.misc.Misc;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class NonManager implements Listener {

	public static List<Non> nons = new ArrayList<>();
	public static List<Player> hitCooldownList = new ArrayList<>();

	static {

		new BukkitRunnable() {
			@Override
			public void run() {
				for(Non non : nons) non.tick();
			}
		}.runTaskTimer(PitRemake.INSTANCE, 0L, 1L);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDamage(EntityDamageByEntityEvent event) {

		if(!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player)) return;
		Player defender = (Player) event.getEntity();

		for(Non non : nons) {

			if(!non.non.getUniqueId().equals(defender.getUniqueId())) continue;

			if(hitCooldownList.contains(non.non)) {

				event.setCancelled(true);
				continue;
			}

			hitCooldownList.add(non.non);
			new BukkitRunnable() {
				@Override
				public void run() {
					hitCooldownList.remove(non.non);
				}
			}.runTaskLater(PitRemake.INSTANCE, 10L);

			if(event.getFinalDamage() < defender.getHealth()) return;
			Misc.multiKill((Player) event.getDamager());
			event.setCancelled(true);
			non.respawn();

			break;
		}
	}
}
