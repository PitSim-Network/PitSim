package dev.kyro.pitsim.controllers;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.boosters.GoldBooster;
import dev.kyro.pitsim.boosters.XPBooster;
import dev.kyro.pitsim.controllers.objects.Booster;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.*;

public class BoosterManager implements Listener {
	public static List<Booster> boosterList = new ArrayList<>();
	public static Map<UUID, List<BoosterReward>> donatorMessages = new HashMap<>();

	class BoosterReward {
		public Booster booster;
		public double amount;

		public BoosterReward(Booster booster, double amount) {
			this.booster = booster;
			this.amount = amount;
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

				boolean boosterEnabled = false;
				for(Booster booster : boosterList) {
					if(booster.minutes == 0) continue;
					boosterEnabled = true;
					booster.minutes--;
					if(booster.minutes == 0) booster.disable();
					else booster.updateTime();
				}
				if(boosterEnabled) FirestoreManager.CONFIG.save();
			}
		}.runTaskTimer(PitSim.INSTANCE, Misc.getRunnableOffset(1), 20 * 60);


		new BukkitRunnable() {
			@Override
			public void run() {
				if(getActiveBoosters() == 0) return;
				if(getActiveBoosters() == 1) {
					Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6&lBOOSTER! &eThere is currently &f1 &eactive booster on the server!"));
				} else
					Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6&lBOOSTER! &eThere are currently &f" + getActiveBoosters() + " &eactive boosters on the server!"));

				TextComponent nonClick = new TextComponent(ChatColor.translateAlternateColorCodes('&', "&eView active boosters by clicking "));
				TextComponent click = new TextComponent(ChatColor.translateAlternateColorCodes('&', "&6&lhere."));
				click.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/booster"));

				nonClick.addExtra(click);

				for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
					onlinePlayer.sendMessage(nonClick);
					Sounds.BOOSTER_REMIND.play(onlinePlayer);
				}

			}
		}.runTaskTimer(PitSim.INSTANCE, Misc.getRunnableOffset(5), (60 * 5) * 20);
	}

	public static void registerBooster(Booster booster) {
		boosterList.add(booster);
		Bukkit.getServer().getPluginManager().registerEvents(booster, PitSim.INSTANCE);
	}

	public static Booster getBooster(String refName) {
		for(Booster booster : boosterList) if(booster.refName.equalsIgnoreCase(refName)) return booster;
		return null;
	}

	public static int getActiveBoosters() {
		int active = 0;
		for(Booster booster : boosterList) {
			if(booster.minutes > 0) active++;
		}
		return active;
	}
}
