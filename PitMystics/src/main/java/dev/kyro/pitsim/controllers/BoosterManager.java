package dev.kyro.pitsim.controllers;

import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.boosters.GoldBooster;
import dev.kyro.pitsim.boosters.XPBooster;
import dev.kyro.pitsim.controllers.objects.Booster;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.misc.Sounds;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.*;

public class BoosterManager implements Listener {
	public static List<Booster> boosterList = new ArrayList<>();
	public static Map<Booster, List<UUID>> donators = new HashMap<>();
	public static Map<UUID, List<BoosterReward>> donatorMessages = new HashMap<>();

	class BoosterReward {
		public Booster booster;
		public double amount;

		public BoosterReward(Booster booster, double amount) {
			this.booster = booster;
			this.amount = amount;
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onKill(KillEvent killEvent) {
		if(killEvent.killer.isOp()) return;

		Booster goldBooster = BoosterManager.getBooster("gold");
		donators.putIfAbsent(goldBooster, new ArrayList<>());
		double gold = killEvent.getFinalGold();
		for(UUID uuid : donators.get(goldBooster)) {
			if(killEvent.killer.getUniqueId().equals(uuid)) continue;
			gold *= (1.0 / 10.0);
			donatorMessages.putIfAbsent(uuid, new ArrayList<>());
			donatorMessages.get(uuid).add(new BoosterReward(goldBooster, gold));
			OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
			PitSim.VAULT.depositPlayer(offlinePlayer, gold);

			boolean isOnline = false;
			for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
				if(!onlinePlayer.getUniqueId().equals(uuid)) continue;
				isOnline = true;
				PitPlayer pitPlayer = PitPlayer.getPitPlayer(onlinePlayer);
				pitPlayer.goldGrinded += gold;
				FileConfiguration playerData = APlayerData.getPlayerData(onlinePlayer);
				playerData.set("goldgrinded", pitPlayer.goldGrinded);
				APlayerData.savePlayerData(onlinePlayer);
			}
			if(!isOnline) {
				FileConfiguration playerData = APlayerData.getPlayerData(uuid);
				playerData.set("goldgrinded", playerData.getInt("goldgrinded") + gold);
				APlayerData.savePlayerData(uuid);
			}
		}

		Booster xpBooster = BoosterManager.getBooster("xp");
		donators.putIfAbsent(xpBooster, new ArrayList<>());
		int xp = killEvent.getFinalXp();
		for(UUID uuid : donators.get(xpBooster)) {
			if(killEvent.killer.getUniqueId().equals(uuid)) continue;
			xp *= (1.0 / 10.0);
			donatorMessages.putIfAbsent(uuid, new ArrayList<>());
			donatorMessages.get(uuid).add(new BoosterReward(xpBooster, xp));
			for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
				if(!onlinePlayer.getUniqueId().equals(uuid)) continue;
				LevelManager.addXp(onlinePlayer, xp);
			}
		}
	}

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				DecimalFormat format = new DecimalFormat("0.#");
				Booster xpBooster = BoosterManager.getBooster("xp");
				Booster goldBooster = BoosterManager.getBooster("gold");
				for(Map.Entry<UUID, List<BoosterReward>> entry : donatorMessages.entrySet()) {
					for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
						if(!onlinePlayer.getUniqueId().equals(entry.getKey())) continue;
						double xp = 0;
						double gold = 0;
						for(BoosterReward boosterReward : entry.getValue()) {
							if(boosterReward.booster.getClass() == XPBooster.class) xp += boosterReward.amount;
							if(boosterReward.booster.getClass() == GoldBooster.class) gold += boosterReward.amount;
						}
						if(xp != 0) {
							AOutput.send(onlinePlayer, "&6&lBOOSTER &7Received &b" +
									format.format(xp) + " XP &7from your " + xpBooster.color + xpBooster.name);
						}
						if(gold != 0) {
							AOutput.send(onlinePlayer, "&6&lBOOSTER &7Received &6" +
									format.format(gold) + "g &7from your " + goldBooster.color + goldBooster.name);
						}
					}
				}
				donatorMessages.clear();

				for(Booster booster : boosterList) {
					if(booster.minutes == 0) continue;
					booster.minutes--;
					if(booster.minutes == 0) booster.disable(); else booster.updateTime();
				}

				donators.clear();
//				Map<Booster, List<UUID>> toAddDonators = new HashMap<>();
				for(Map.Entry<UUID, FileConfiguration> entry : APlayerData.getAllData().entrySet()) {
					FileConfiguration playerData = entry.getValue();
					UUID uuid = entry.getKey();
					boolean changed = false;

					for(Booster booster : boosterList) {
						int timeLeft = playerData.getInt("booster-time." + booster.refName);
						if(timeLeft <= 0) continue;

						playerData.set("booster-time." + booster.refName, --timeLeft);

						donators.putIfAbsent(booster, new ArrayList<>());
						donators.get(booster).add(uuid);

						changed = true;
					}

					if(changed) APlayerData.savePlayerData(uuid);
				}

//				for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
//					for(Map.Entry<Booster, List<UUID>> entry : toAddDonators.entrySet()) {
//						if(!entry.getValue().contains(onlinePlayer.getUniqueId())) continue;
//						donators.putIfAbsent(entry.getKey(), new ArrayList<>());
//						donators.get(entry.getKey()).add(onlinePlayer);
//					}
//				}
			}
//		}.runTaskTimer(PitSim.INSTANCE, 20, 20);
		}.runTaskTimer(PitSim.INSTANCE, 20 * 60L, 20 * 60);


		new BukkitRunnable() {
			@Override
			public void run() {
				if(getActiveBoosters() == 0) return;
				if(getActiveBoosters() == 1) {
					Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6&lBOOSTER! &eThere is currently &f1 &eactive booster on the server!"));
				}  else Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6&lBOOSTER! &eThere are currently &f" + getActiveBoosters() + " &eactive boosters on the server!"));

				TextComponent nonClick = new TextComponent(ChatColor.translateAlternateColorCodes('&',"&eView active boosters by clicking "));
				TextComponent click = new TextComponent(ChatColor.translateAlternateColorCodes('&', "&6&lhere."));
				click.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/booster"));

				nonClick.addExtra(click);

				for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
					onlinePlayer.sendMessage(nonClick);
					Sounds.BOOSTER_REMIND.play(onlinePlayer);
				}

			}
		}.runTaskTimer(PitSim.INSTANCE, (60 * 5) * 20, (60 * 5) * 20);
	}

	public static void registerBooster(Booster booster) {
		boosterList.add(booster);
		Bukkit.getServer().getPluginManager().registerEvents(booster, PitSim.INSTANCE);
	}

	public static Booster getBooster(String refName) {
		for(Booster booster : boosterList) if(booster.refName.equalsIgnoreCase(refName)) return booster;
		return null;
	}

	public static void addTime(Booster booster, int minutes) {
		if(minutes == 0) return;
		booster.minutes += minutes;
		booster.updateTime();
	}

	public static int getActiveBoosters() {
		int active = 0;
		for(Booster booster : boosterList) {
			if(booster.minutes > 0) active++;
		}
		return active;
	}
}
