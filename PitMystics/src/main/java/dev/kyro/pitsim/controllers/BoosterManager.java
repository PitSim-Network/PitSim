package dev.kyro.pitsim.controllers;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.Booster;
import dev.kyro.pitsim.misc.Sounds;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class BoosterManager implements Listener {
	public static List<Booster> boosterList = new ArrayList<>();

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				for(Booster booster : boosterList) {
					if(booster.minutes == 0) continue;
					booster.minutes--;
					if(booster.minutes == 0) booster.disable(); else booster.updateTime();
				}
			}
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
