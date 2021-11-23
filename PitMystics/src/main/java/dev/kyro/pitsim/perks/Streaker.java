package dev.kyro.pitsim.perks;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PitPerk;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.events.OofEvent;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.*;

public class Streaker extends PitPerk {
	public static Map<Player, Integer> playerTimes = new HashMap<>();
	public Map<Player, Double> xpReward = new HashMap<>();

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
		killEvent.xpCap += 80;

		if(xpReward.containsKey(killEvent.killer)) killEvent.xpMultipliers.add(xpReward.get(killEvent.killer));

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(killEvent.killer);

		if(pitPlayer.getKills() + 1 >= pitPlayer.megastreak.getRequiredKills()) {
			if(playerTimes.containsKey(killEvent.killer)) {
				Player player = killEvent.killer;
				double xp = 0;

//				TODO: Update lore
				if(playerTimes.get(player) > 100) xp = 1;
				if(playerTimes.get(player) <= 100) xp = 1.1;
				if(playerTimes.get(player) <= 80) xp = 1.2;
				if(playerTimes.get(player) <= 70) xp = 1.3;
				if(playerTimes.get(player) <= 60) xp = 1.4;
				if(playerTimes.get(player) <= 50) xp = 1.5;
				if(playerTimes.get(player) <= 40) xp = 1.6;
				if(playerTimes.get(player) <= 35) xp = 1.7;
				if(playerTimes.get(player) <= 30) xp = 1.8;
				if(playerTimes.get(player) <= 25) xp = 1.9;
				if(playerTimes.get(player) <= 20) xp = 2;

				xpReward.put(player, xp);

				DecimalFormat format = new DecimalFormat("0.#");
				AOutput.send(player, "&b&lSTREAKER! &7You hit your megastreak in &e" +
						playerTimes.get(player) + " seconds&7. Gain &b+" + format.format(Math.ceil((xp - 1) * 100)) + "% XP &7for the rest of the streak.");
				Sounds.STREAKER.play(player);
				playerTimes.remove(player);
				return;
			}
		}

		if(!playerTimes.containsKey(killEvent.killer) && !pitPlayer.megastreak.isOnMega()) {
			playerTimes.put(killEvent.killer, 0);
			AOutput.send(killEvent.killer, "&b&lSTREAKER! &7Streak timer started!");
			Sounds.STREAKER.play(killEvent.killer);
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

	@EventHandler
	public void onOof(OofEvent event) {
		xpReward.remove(event.getPlayer());
		playerTimes.remove(event.getPlayer());
	}

	@Override
	public List<String> getDescription() {
		return new ALoreBuilder("&7Upon reaching your &emegastreak&7,",
				"&7gain &bmore XP the faster you hit mega", "&7Passively gain &b+80 max XP").getLore();
	}
}
