package dev.kyro.pitsim.controllers;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.Killstreak;
import dev.kyro.pitsim.controllers.objects.Megastreak;
import dev.kyro.pitsim.controllers.objects.PitPerk;

import java.util.ArrayList;
import java.util.List;

public class PerkManager {

	public static List<PitPerk> pitPerks = new ArrayList<>();
	public static List<Megastreak> megastreaks = new ArrayList<>();
	public static List<Killstreak> killstreaks = new ArrayList<>();

	public static void registerUpgrade(PitPerk pitPerk) {

		pitPerks.add(pitPerk);
		PitSim.INSTANCE.getServer().getPluginManager().registerEvents(pitPerk, PitSim.INSTANCE);
	}

	public static void registerMegastreak(Megastreak megastreak) {
		megastreaks.add(megastreak);

	}

	public static void registerKillstreak(Killstreak killstreak) {
		killstreaks.add(killstreak);
		PitSim.INSTANCE.getServer().getPluginManager().registerEvents(killstreak, PitSim.INSTANCE);
	}


}
