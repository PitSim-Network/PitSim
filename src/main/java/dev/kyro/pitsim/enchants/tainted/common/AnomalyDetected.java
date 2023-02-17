package dev.kyro.pitsim.enchants.tainted.common;

import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.misc.PitLoreBuilder;

import java.util.List;

public class AnomalyDetected extends PitEnchant {
	public static AnomalyDetected INSTANCE;

	public AnomalyDetected() {
		super("Anomaly Detected!", false, ApplyType.SCYTHES,
				"anamolydetected", "anamoly", "anomalydetected", "anomaly");
		isTainted = true;
		INSTANCE = this;
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {

		return new PitLoreBuilder(
				"&7A basic tainted enchant"
		).getLore();
	}
}
