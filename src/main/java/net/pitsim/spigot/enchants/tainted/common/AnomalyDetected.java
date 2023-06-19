package net.pitsim.spigot.enchants.tainted.common;

import net.pitsim.spigot.darkzone.PitMob;
import net.pitsim.spigot.darkzone.mobs.PitWitherSkeleton;
import net.pitsim.spigot.darkzone.mobs.PitZombiePigman;
import net.pitsim.spigot.controllers.objects.BasicDarkzoneEnchant;
import net.pitsim.spigot.enums.ApplyType;

import java.util.Arrays;
import java.util.List;

public class AnomalyDetected extends BasicDarkzoneEnchant {
	public static AnomalyDetected INSTANCE;

	public AnomalyDetected() {
		super("Anomaly Detected!", false, ApplyType.SCYTHES,
				"anomalydetected", "anomaly");
		isTainted = true;
		INSTANCE = this;
	}

	@Override
	public int getBaseStatPercent(int enchantLvl) {
		return enchantLvl * 9 + 8;
	}

	@Override
	public boolean isOffensive() {
		return true;
	}

	@Override
	public List<Class<? extends PitMob>> getApplicableMobs() {
		return Arrays.asList(PitZombiePigman.class, PitWitherSkeleton.class);
	}
}
