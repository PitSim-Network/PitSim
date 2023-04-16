package dev.kyro.pitsim.controllers;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.*;
import dev.kyro.pitsim.perks.NoPerk;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PerkManager {
	public static List<PitPerk> pitPerks = new ArrayList<>();
	public static List<Megastreak> megastreaks = new ArrayList<>();
	public static List<Killstreak> killstreaks = new ArrayList<>();

	public static void registerUpgrade(PitPerk pitPerk) {
		pitPerks.add(pitPerk);
		if(PitSim.status.isOverworld()) PitSim.INSTANCE.getServer().getPluginManager().registerEvents(pitPerk, PitSim.INSTANCE);
	}

	public static void registerMegastreak(Megastreak megastreak) {
		megastreaks.add(megastreak);
	}

	public static void registerKillstreak(Killstreak killstreak) {
		killstreaks.add(killstreak);
		PitSim.INSTANCE.getServer().getPluginManager().registerEvents(killstreak, PitSim.INSTANCE);
	}

	public static ChatColor getChatColor(Player player, PitPerk pitPerk) {
		if(!isUnlocked(player, pitPerk)) return ChatColor.RED;
		if(isEquipped(player, pitPerk)) return ChatColor.GREEN;
		return ChatColor.YELLOW;
	}

	public static boolean isEquipped(Player player, PitPerk pitPerk) {
		if(!isUnlocked(player, pitPerk)) return false;
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		for(PitPerk testPerk : pitPlayer.pitPerks) if(testPerk == pitPerk) return true;
		return false;
	}

	public static boolean isUnlocked(Player player, PitPerk pitPerk) {
		if(pitPerk.renownUpgradeClass == null) return true;
		RenownUpgrade upgrade = UpgradeManager.getUpgrade(pitPerk.renownUpgradeClass);
		return UpgradeManager.hasUpgrade(player, upgrade);
	}

	public static PitPerk getPitPerk(String refName) {
		for(PitPerk pitPerk : pitPerks) if(pitPerk.refName.equalsIgnoreCase(refName)) return pitPerk;
		return NoPerk.INSTANCE;
	}
}
