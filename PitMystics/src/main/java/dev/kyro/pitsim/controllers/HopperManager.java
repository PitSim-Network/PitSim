package dev.kyro.pitsim.controllers;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.Hopper;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.events.OofEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class HopperManager implements Listener {
	public static List<Hopper> hopperList = new ArrayList<>();
	public static List<Hopper> toRemove = new ArrayList<>();

	public HopperManager() {

		new BukkitRunnable() {
			@Override
			public void run() {
				for(Hopper hopper : hopperList) {
//					if(!hopper.npc.isSpawned() && hopper.count > 20) toRemove.add(hopper);
					hopper.tick();
				}
				for(Hopper hopper : toRemove) hopperList.remove(hopper);
				toRemove.clear();
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 1L);
	}

	public static Hopper callHopper(String name, Hopper.Type type, Player target) {
		Hopper hopper = new Hopper(name, type, target);
		hopperList.add(hopper);
		return hopper;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(isHopper(attackEvent.defender)) {
			Hopper hopper = getHopper(attackEvent.defender);
			attackEvent.multiplier.add(hopper.type.damageMultiplier);
			if(attackEvent.arrow != null || attackEvent.pet != null) {
				attackEvent.multiplier.add(0D);
				attackEvent.trueDamage = 0;
			}
		}
		if(isHopper(attackEvent.attacker)) {
			Hopper hopper = getHopper(attackEvent.attacker);
			if(hopper.type != Hopper.Type.CHAIN) {
				PitPlayer pitHopper = PitPlayer.getPitPlayer(hopper.hopper);
				double amount = 1;
				if(hopper.target != null && hopper.target != attackEvent.defender) pitHopper.heal(amount / 2.0);
				pitHopper.heal(amount);
			}
		}
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		for(Hopper hopper : hopperList) {
			if(killEvent.dead != hopper.target) continue;
			hopper.remove();
		}

		if(isHopper(killEvent.dead)) {
			Hopper hopper = HopperManager.getHopper(killEvent.dead);
			hopper.remove();
		}
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		for(Hopper hopper : hopperList) {
			if(event.getPlayer() != hopper.target) continue;
			hopper.remove();
		}
	}

	@EventHandler
	public void onChangeWorld(PlayerChangedWorldEvent event) {
		for(Hopper hopper : hopperList) {
			if(event.getPlayer() != hopper.target || event.getPlayer().getWorld() == hopper.hopper.getWorld()) continue;
			hopper.remove();
		}
	}

	@EventHandler
	public void onOof(OofEvent event) {
		for(Hopper hopper : hopperList) {
			if(event.getPlayer() != hopper.target) continue;
			hopper.remove();
		}
	}

	public static boolean isHopper(Player player) {
		return getHopper(player) != null;
	}

	public static Hopper getHopper(Player player) {
		for(Hopper hopper : hopperList) if(hopper.hopper == player) return hopper;
		return null;
	}
}
