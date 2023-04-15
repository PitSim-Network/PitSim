package dev.kyro.pitsim.perks;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.NonManager;
import dev.kyro.pitsim.controllers.SpawnManager;
import dev.kyro.pitsim.controllers.objects.PitPerk;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import dev.kyro.pitsim.misc.Sounds;
import dev.kyro.pitsim.upgrades.UnlockFirstStrike;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class Streaker extends PitPerk {
	public static Map<LivingEntity, Integer> playerTimes = new HashMap<>();
	public static Map<LivingEntity, Double> xpReward = new HashMap<>();

	public static Streaker INSTANCE;

	public Streaker() {
		super("Streaker", "streaker");
		renownUpgradeClass = UnlockFirstStrike.class;
		INSTANCE = this;
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		xpReward.remove(killEvent.getDead());
		playerTimes.remove(killEvent.getDead());

		if(!hasPerk(killEvent.getKiller())) return;
		if(!killEvent.isDeadPlayer() || NonManager.getNon(killEvent.getDead()) == null) return;
		killEvent.xpCap += 80;

		if(xpReward.containsKey(killEvent.getKiller()))
			killEvent.xpMultipliers.add(xpReward.get(killEvent.getKiller()));

		if(!killEvent.isKillerPlayer()) return;
		PitPlayer pitPlayer = killEvent.getKillerPitPlayer();

		if(pitPlayer.getKills() + 1 >= pitPlayer.megastreak.getRequiredKills()) {
			if(playerTimes.containsKey(killEvent.getKiller())) {
				Player player = killEvent.getKillerPlayer();
				double xp = 0;

				if(playerTimes.get(player) > 90) xp = 1;
				if(playerTimes.get(player) <= 90) xp = 1.1;
				if(playerTimes.get(player) <= 80) xp = 1.2;
				if(playerTimes.get(player) <= 60) xp = 1.3;
				if(playerTimes.get(player) <= 45) xp = 1.4;
				if(playerTimes.get(player) <= 30) xp = 1.5;
				if(playerTimes.get(player) <= 25) xp = 1.6;
				if(playerTimes.get(player) <= 20) xp = 1.7;
				if(playerTimes.get(player) <= 15) xp = 1.8;
				if(playerTimes.get(player) <= 10) xp = 1.9;
				if(playerTimes.get(player) <= 5) xp = 2;

				xpReward.put(player, xp);

				DecimalFormat format = new DecimalFormat("0.#");
				AOutput.send(player, "&b&lSTREAKER!&7 You hit your megastreak in &e" +
						playerTimes.get(player) + " seconds&7. Gain &b+" + format.format(Math.ceil((xp - 1) * 100)) + "% XP &7for the rest of the streak.");
				Sounds.STREAKER.play(player);
				playerTimes.remove(player);
				return;
			}
		}

		if(!playerTimes.containsKey(killEvent.getKiller()) && !pitPlayer.megastreak.isOnMega() && !SpawnManager.isInSpawn(pitPlayer.player)) {
			playerTimes.put(killEvent.getKiller(), 0);
			Sounds.STREAKER.play(killEvent.getKiller());
			AOutput.send(killEvent.getKiller(), "&b&lSTREAKER!&7 Streak timer started!");
		}
	}

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				for(Map.Entry<LivingEntity, Integer> entry : playerTimes.entrySet()) {
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
	public ItemStack getBaseDisplayStack() {
		return new AItemStackBuilder(Material.WHEAT)
				.getItemStack();
	}

	@Override
	public PitLoreBuilder getBaseDescription() {
		return new PitLoreBuilder(
				"&7Upon reaching your &cMegastreak&7, gain &bmore XP &7the faster you hit mega. " +
						"Passively gain &b+80 max XP"
		);
	}

	@Override
	public String getSummary() {
		return "&eStreaker &7is a perk unlocked in the &erenown shop&7 that gives you a higher &bXP cap &7and more " +
				"&bXP &7based on how quickly you activate a &cMegastreak";
	}
}
