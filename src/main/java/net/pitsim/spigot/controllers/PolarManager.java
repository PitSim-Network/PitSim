package net.pitsim.spigot.controllers;

import net.pitsim.spigot.PitSim;
import net.pitsim.spigot.controllers.objects.AnticheatManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import top.polar.api.check.Check;
import top.polar.api.user.User;
import top.polar.api.user.event.DetectionAlertEvent;
import top.polar.api.user.event.MitigationEvent;
import top.polar.api.user.event.type.CheckType;

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
		if(event.chatAlertMessage().contains("Clicking suspiciously")) event.cancelled(true);
	}

	@EventHandler
	public void onMitigate(MitigationEvent event) {
		User user = event.user();
		if(user == null || !user.bukkitPlayer().isPresent()) return;
		Player player = event.user().bukkitPlayer().get();

		CheckType mitigationType = event.check().type();
		if(!isExempt(player, FlagType.getFlagPolar(mitigationType.name()))) return;

		event.cancelled(true);
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
