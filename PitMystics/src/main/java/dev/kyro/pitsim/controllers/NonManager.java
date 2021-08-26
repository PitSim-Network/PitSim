package dev.kyro.pitsim.controllers;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.commands.ATestCommand;
import dev.kyro.pitsim.controllers.objects.Non;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class NonManager implements Listener {

	public static List<Non> nons = new ArrayList<>();

	static {

		new BukkitRunnable() {
			@Override
			public void run() {
//				if(true) return;
				if(nons.size() >= 25) return;
				Non non = new Non(ATestCommand.hoppers.get((int) (Math.random() * ATestCommand.hoppers.size())));
				new BukkitRunnable() {
					@Override
					public void run() {
						non.remove();
					}
				}.runTaskLater(PitSim.INSTANCE, (long) (20 * 60 * (Math.random() * 4 + 1)));
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 40);

		new BukkitRunnable() {
			@Override
			public void run() {
				if(!PitEventManager.majorEvent) for(Non non : nons) non.tick();
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 1L);
	}

//	@EventHandler(priority = EventPriority.LOW)
	public void onDamage(EntityDamageByEntityEvent event) {

		if(!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player)) return;
		Player defender = (Player) event.getEntity();

		Non non = getNon(defender);
		if(non == null) return;

		if(DamageManager.hitCooldownList.contains(non.non)) {

			event.setCancelled(true);
			return;
		}

		if(event.getFinalDamage() < defender.getHealth()) return;
	}

	public static Non getNon(Player player) {
		if(PitEventManager.majorEvent) return null;

		for(Non non : nons) {

			if(non == null) continue;
			if(non.non.getUniqueId().equals(player.getUniqueId())) return non;
		}
		return null;
	}
}
