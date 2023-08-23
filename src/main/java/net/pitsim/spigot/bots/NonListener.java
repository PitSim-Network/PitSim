package net.pitsim.spigot.bots;

import net.pitsim.spigot.PitSim;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NonListener implements Listener {
	public List<UUID> attacked = new ArrayList<>();

	@EventHandler
	public void onFall(EntityDamageEvent event) {
		if(event.getCause() == EntityDamageEvent.DamageCause.FALL || event.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION) event.setCancelled(true);
	}

	@EventHandler
	public void onEntityAttack(EntityDamageByEntityEvent event) {
		if(!(event.getEntity() instanceof Player)) return;
		Player damaged = (Player) event.getEntity();
		Non non = NonManager.getNon(damaged);
		if(non == null) return;

		if(attacked.contains(damaged.getUniqueId())) {
			event.setCancelled(true);
			return;
		}

		attacked.add(damaged.getUniqueId());

		new BukkitRunnable() {
			@Override
			public void run() {
				attacked.remove(damaged.getUniqueId());
			}
		}.runTaskLater(PitSim.INSTANCE, 10);

		if(event.getFinalDamage() > damaged.getHealth()) {
			event.setCancelled(true);
			non.respawn(false);
		}

	}
}
