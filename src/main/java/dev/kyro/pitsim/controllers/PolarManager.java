package dev.kyro.pitsim.controllers;

import ac.grim.grimac.AbstractCheck;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.AnticheatManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import top.polar.api.event.DetectionAlertEvent;
import top.polar.api.event.MitigationEvent;
import top.polar.api.mitigation.MitigationType;

import java.util.*;

public class PolarManager extends AnticheatManager implements Listener {

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
	public void onFlag(DetectionAlertEvent event) {
		if(event.getChatMessage().contains("Clicking suspiciously")) event.setCancelled(true);
	}

	@EventHandler
	public void onMitigate(MitigationEvent event) {
		Player player = event.getPlayer();
		if(player == null) return;
		MitigationType mitigationType = event.getMitigationType();
		if(!isExempt(player, FlagType.getFlagPolar(mitigationType.name()))) return;

//		System.out.println(player.getName() + " was exempted from flag " + check.getCheckName());
		event.setCancelled(true);
	}

	@Override
	public void exemptPlayer(Player player, long ticks, AnticheatManager.FlagType... flags) {
		exemptPlayers.add(new Exemption(player, ticks, Arrays.asList(flags)));
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
