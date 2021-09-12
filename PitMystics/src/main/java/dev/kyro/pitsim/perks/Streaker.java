package dev.kyro.pitsim.perks;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.ASound;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PitPerk;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.events.KillEvent;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Streaker extends PitPerk {
	public static Map<Player, Integer> playerTimes = new HashMap<>();
	public Map<Player, Integer> xpReward = new HashMap<>();

	public static Streaker INSTANCE;

	public Streaker() {
		super("Streaker", "streaker", new ItemStack(Material.WHEAT), 19, true, "STREAKER", INSTANCE);
		INSTANCE = this;
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {

		xpReward.remove(killEvent.dead);
		playerTimes.remove(killEvent.dead);

		if(!playerHasUpgrade(killEvent.killer)) return;

		if(xpReward.containsKey(killEvent.killer)) killEvent.xpCap += xpReward.get(killEvent.killer);

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(killEvent.killer);

		if(pitPlayer.megastreak.isOnMega()) {
			if(playerTimes.containsKey(killEvent.killer)) {
				Player player = killEvent.killer;
				int xp = 0;

				if(playerTimes.get(player) > 120) xp  = 10;
				if(playerTimes.get(player) <= 120) xp  = 10;
				if(playerTimes.get(player) <= 110) xp  = 20;
				if(playerTimes.get(player) <= 100) xp  = 30;
				if(playerTimes.get(player) <= 90) xp  = 40;
				if(playerTimes.get(player) <= 80) xp  = 50;
				if(playerTimes.get(player) <= 70) xp  = 60;
				if(playerTimes.get(player) <= 60) xp  = 70;
				if(playerTimes.get(player) <= 50) xp  = 80;
				if(playerTimes.get(player) <= 40) xp  = 90;
				if(playerTimes.get(player) <= 30) xp  = 100;

				 xpReward.put(player, xp);

				AOutput.send(player, "&b&lSTREAKER! &7You hit your megastreak in &e" +
						playerTimes.get(player) + " seconds&7. Gained &b+" + xp + " max XP &7for the rest of the streak.");
				ASound.play(player, Sound.BURP, 2, 1.2F);
				playerTimes.remove(player);
				return;
			}
		}

		if(!playerTimes.containsKey(killEvent.killer) && !pitPlayer.megastreak.isOnMega()) {
			playerTimes.put(killEvent.killer, 0);
			AOutput.send(killEvent.killer, "&b&lSTREAKER! &7Streak timer started!");
			ASound.play(killEvent.killer, Sound.BURP, 2, 1.2F);
		}
	}

	static {

		new BukkitRunnable() {
			@Override
			public void run() {

				List<UUID> toRemove = new ArrayList<>();
				for(Map.Entry<Player, Integer> entry : playerTimes.entrySet()) {
					int time = entry.getValue();
					time = time + 1;

					playerTimes.put(entry.getKey(), time);
				}

			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 20L);
	}
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {

		xpReward.remove(event.getPlayer());
		playerTimes.remove(event.getPlayer());

	}

	@Override
	public List<String> getDescription() {
		return new ALoreBuilder("&7Upon reaching your &emegastreak&7,", "&7gain &b+100 max XP &7if it took &f30 &7or",
				"&7less seconds. Subtracts &b10 max", "&bXP &7per additional &f10 &7seconds.").getLore();
	}
}
