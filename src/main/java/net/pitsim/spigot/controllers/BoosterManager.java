package net.pitsim.spigot.controllers;

import dev.kyro.arcticapi.misc.AOutput;
import net.pitsim.spigot.PitSim;
import net.pitsim.spigot.boosters.GoldBooster;
import net.pitsim.spigot.boosters.XPBooster;
import net.pitsim.spigot.controllers.objects.Booster;
import net.pitsim.spigot.controllers.objects.PitPlayer;
import net.pitsim.spigot.controllers.objects.PluginMessage;
import net.pitsim.spigot.misc.Misc;
import net.pitsim.spigot.misc.Sounds;
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
				for(Map.Entry<UUID, List<BoosterReward>> entry : donatorMessages.entrySet()) {
					for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
						if(!onlinePlayer.getUniqueId().equals(entry.getKey())) continue;
						double xp = 0;
						double gold = 0;
						for(BoosterReward boosterReward : entry.getValue()) {
							if(boosterReward.booster instanceof XPBooster) xp += boosterReward.amount;
							if(boosterReward.booster instanceof GoldBooster) gold += boosterReward.amount;
						}
						if(xp != 0) {
							AOutput.send(onlinePlayer, "&6&lBOOSTER &7Received &b" +
									format.format(xp) + " XP &7from your " + XPBooster.INSTANCE.color + XPBooster.INSTANCE.name);
						}
						if(gold != 0) {
							AOutput.send(onlinePlayer, "&6&lBOOSTER &7Received &6" +
									format.format(gold) + "g &7from your " + GoldBooster.INSTANCE.color + GoldBooster.INSTANCE.name);
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
				if(boosterEnabled) {
					for(Player player : ChatTriggerManager.getSubscribedPlayers()) ChatTriggerManager.sendBoosterInfo(PitPlayer.getPitPlayer(player));
					FirestoreManager.CONFIG.save();
				}
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

		new BukkitRunnable() {
			@Override
			public void run() {
				for(Booster booster : boosterList) {
					if(booster.activatorUUID == null || booster.toShare == 0) continue;

					int toShare = (int) booster.toShare;
					Player activator = Bukkit.getPlayer(booster.activatorUUID);
					if(activator != null) {
						booster.queueOnlineShare(activator, toShare);
					} else {
						new PluginMessage().writeString("BOOSTER_SHARE")
								.writeString(booster.refName)
								.writeString(booster.activatorUUID.toString())
								.writeInt(toShare)
								.send();
					}

					booster.toShare = 0;
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 20 * 10);
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
		for(Booster booster : boosterList) if(booster.isActive()) active++;
		return active;
	}
}
