package dev.kyro.pitsim.controllers;

import dev.kyro.pitsim.PitSim;

import java.util.ArrayList;
import java.util.List;

public class UpgradeManager {

	public static List<PitUpgrade> pitUpgrades = new ArrayList<>();

	public static void registerUpgrade(PitUpgrade pitUpgrade) {

		pitUpgrades.add(pitUpgrade);
		PitSim.INSTANCE.getServer().getPluginManager().registerEvents(pitUpgrade, PitSim.INSTANCE);
	}
}
