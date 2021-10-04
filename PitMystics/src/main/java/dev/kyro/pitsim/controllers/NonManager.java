package dev.kyro.pitsim.controllers;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.Non;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class NonManager implements Listener {
	public static List<String> botIGNs = new ArrayList<>();
	public static List<Non> nons = new ArrayList<>();

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
//				if(true) return;
				if(botIGNs.isEmpty()) botIGNs.add("KyroKrypt");
				if(nons.size() >= getMaxNons()) return;
				Non non = new Non(botIGNs.get((int) (Math.random() * botIGNs.size())));
				new BukkitRunnable() {
					@Override
					public void run() {
						non.remove();
					}
				}.runTaskLater(PitSim.INSTANCE, (long) (20 * 60));
			}
		}.runTaskTimer(PitSim.INSTANCE, 40L, 20);

		new BukkitRunnable() {
			@Override
			public void run() {
				if(!PitEventManager.majorEvent) for(Non non : nons) non.tick();
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 1L);
	}

	public static int getMaxNons() {
		Location mid = MapManager.getMid();
		int playersNearMid = 0;
		for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			if(onlinePlayer.getWorld() != mid.getWorld()) continue;
			double distance = mid.distance(onlinePlayer.getLocation());
			if(distance < 20) playersNearMid++;
		}
		return playersNearMid * 5 + 10;
	}

	public static void updateNons(List<String> botIGNs) {

		NonManager.botIGNs = new ArrayList<>(botIGNs);
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

		try {
			for(Non non : nons) {
				if(non == null) continue;
				if(non.non.getUniqueId().equals(player.getUniqueId())) return non;
			}
		} catch(Exception exception) {
			return null;
		}
		return null;
	}
}
