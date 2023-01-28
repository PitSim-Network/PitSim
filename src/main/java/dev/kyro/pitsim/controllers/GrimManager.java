package dev.kyro.pitsim.controllers;

import ac.grim.grimac.AbstractCheck;
import ac.grim.grimac.events.FlagEvent;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.AnticheatManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GrimManager extends AnticheatManager implements Listener {
	private static final List<Exemption> exemptPlayers = new ArrayList<>();

	public static boolean isExempt(Player player, FlagType... flagsArr) {
		List<FlagType> flags = Arrays.asList(flagsArr);
		for(Exemption exemptPlayer : exemptPlayers) {
			if(exemptPlayer.player != player) continue;
			if(flags.contains(FlagType.ALL)) return true;
			for(FlagType flag : flags) if(exemptPlayer.flags.contains(flag)) return true;
		}
		return false;
	}

	@EventHandler
	public void onFlag(FlagEvent event) {
		Player player = Bukkit.getPlayer(event.getPlayer().getUniqueId());
		if(player == null) return;
		AbstractCheck check = event.getCheck();
		if(!isExempt(player, FlagType.getFlag(check.getCheckName()))) return;

//		System.out.println(player.getName() + " was exempted from flag " + check.getCheckName());
		event.setCancelled(true);
	}

	@Override
	public void exemptPlayer(Player player, long ms, AnticheatManager.FlagType... args) {
		List<AnticheatManager.FlagType> flags = Arrays.asList(args);

		exemptPlayers.add(new Exemption(player, ms, flags));
	}

	private static class Exemption {
		public Player player;
		public List<FlagType> flags;

		public Exemption(Player player, long ticks, List<FlagType> flags) {
			this.player = player;
			this.flags = flags;

			new BukkitRunnable() {
				@Override
				public void run() {
					exemptPlayers.remove(Exemption.this);
				}
			}.runTaskLater(PitSim.INSTANCE, ticks);
		}
	}
}

